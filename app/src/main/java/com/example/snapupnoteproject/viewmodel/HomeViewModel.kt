package com.example.snapupnoteproject.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.snapupnoteproject.data.AuthRepository
import com.example.snapupnoteproject.data.FirestoreRepository
import com.example.snapupnoteproject.model.Card
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
            if (newUid == currentUid) {
                return@addAuthStateListener
            }

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
            cards.clear()
            cards.addAll(list)
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
        firestoreRepo.deleteCard(uid, cardId) { success, msg ->
            cb(success, msg)
        }
    }

    fun logout() {
        authRepo.logout()
    }

    override fun onCleared() {
        authStateListener?.let { authRepo.removeAuthStateListener(it) }
        stopListeningCards()
        super.onCleared()
    }
}
