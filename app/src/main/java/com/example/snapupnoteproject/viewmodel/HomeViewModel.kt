package com.example.snapupnoteproject.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.snapupnoteproject.data.AuthRepository
import com.example.snapupnoteproject.data.FirestoreRepository
import com.example.snapupnoteproject.model.Card
import com.example.snapupnoteproject.model.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration

class HomeViewModel(
    private val authRepo: AuthRepository = AuthRepository(),
    private val firestoreRepo: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    val cards = mutableStateListOf<Card>()
    var currentUid: String? = null
        private set

    private var cardsListener: ListenerRegistration? = null
    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    init {
        currentUid = authRepo.currentUserUid()
        if (currentUid != null) {
            startListeningCards(currentUid!!)
        }

        authStateListener = authRepo.addAuthStateListener { user ->
            val newUid = user?.uid
            if (newUid == currentUid) return@addAuthStateListener

            currentUid = newUid
            stopListeningCards()
            if (currentUid != null) {
                startListeningCards(currentUid!!)
            } else {
                cards.clear()
            }
        }
    }

    private fun startListeningCards(uid: String) {
        stopListeningCards()
        cardsListener = firestoreRepo.listenCards(uid) { list ->
            val sorted = list.sortedWith(
                compareByDescending<Card> { it.pinned }
                    .thenByDescending { it.createdAt?.seconds ?: 0L }
            )
            cards.clear()
            cards.addAll(sorted)
        }
    }

    private fun stopListeningCards() {
        cardsListener?.remove()
        cardsListener = null
    }

    fun deleteCard(cardId: String, cb: (Boolean, String?) -> Unit = { _, _ -> }) {
        val uid = currentUid ?: run {
            cb(false, "Usuário não autenticado")
            return
        }
        firestoreRepo.deleteCard(uid, cardId) { success, msg -> cb(success, msg) }
    }

    fun logout() {
        authRepo.logout()
    }
    fun toggleItemDone(cardId: String, itemIndex: Int) {
        val uid = currentUid ?: return
        val idx = cards.indexOfFirst { it.id == cardId }
        if (idx == -1) return
        val card = cards[idx]
        val itemsMutable = card.items.toMutableList()
        val currentItem = itemsMutable.getOrNull(itemIndex) ?: return

        val newItem = Item(text = currentItem.text, done = !currentItem.done)
        itemsMutable[itemIndex] = newItem

        val updatedCard = card.copy(items = itemsMutable)

        cards[idx] = updatedCard

        firestoreRepo.updateCardFields(uid, cardId, mapOf("items" to itemsMutable)) { success, _ ->
            if (!success) {
                cards[idx] = card
            }
        }
    }

    fun togglePin(cardId: String) {
        val uid = currentUid ?: return
        val idx = cards.indexOfFirst { it.id == cardId }
        if (idx == -1) return

        val card = cards[idx]
        val newPinned = !card.pinned
        val updatedCard = card.copy(pinned = newPinned)

        cards.removeAt(idx)
        if (updatedCard.pinned) {
            cards.add(0, updatedCard)
        } else {
            val insertIndex = cards.indexOfFirst { !it.pinned }.let { if (it == -1) cards.size else it }
            cards.add(insertIndex, updatedCard)
        }

        firestoreRepo.updateCardFields(uid, cardId, mapOf("pinned" to newPinned)) { success, _ ->
            if (!success) {
                val i = cards.indexOfFirst { it.id == cardId }
                if (i != -1) {
                    cards[i] = card
                } else {
                    cards.add(idx, card)
                }
            }
        }
    }

    override fun onCleared() {
        authStateListener?.let { authRepo.removeAuthStateListener(it) }
        stopListeningCards()
        super.onCleared()
    }
}
