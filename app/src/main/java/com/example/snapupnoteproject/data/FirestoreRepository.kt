package com.example.snapupnoteproject.data

import com.example.snapupnoteproject.model.Card
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class FirestoreRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun addCard(uid: String, card: Card, cb: (Boolean, String?) -> Unit) {
        val col = firestore.collection("users").document(uid).collection("cards")
        val ref = col.document()

        val toSave = card.copy(
            id = ref.id,
            ownerId = uid,
            createdAt = Timestamp.now()
        )

        ref.set(toSave)
            .addOnSuccessListener { cb(true, ref.id) }
            .addOnFailureListener { e -> cb(false, e.localizedMessage) }
    }
    fun updateCard(card: Card, cb: (Boolean, String?) -> Unit) {
        if (card.ownerId.isBlank() || card.id.isBlank()) {
            cb(false, "Card sem ownerId ou id")
            return
        }
        val docRef = firestore.collection("users").document(card.ownerId).collection("cards").document(card.id)
        docRef.set(card)
            .addOnSuccessListener { cb(true, null) }
            .addOnFailureListener { e -> cb(false, e.localizedMessage) }
    }
    fun updateCardFields(ownerId: String, cardId: String, fields: Map<String, Any?>, cb: (Boolean, String?) -> Unit) {
        firestore.collection("users").document(ownerId).collection("cards").document(cardId)
            .update(fields)
            .addOnSuccessListener { cb(true, null) }
            .addOnFailureListener { e -> cb(false, e.localizedMessage) }
    }

    fun deleteCard(uid: String, cardId: String, cb: (Boolean, String?) -> Unit) {
        firestore.collection("users").document(uid).collection("cards").document(cardId)
            .delete()
            .addOnSuccessListener { cb(true, null) }
            .addOnFailureListener { e -> cb(false, e.localizedMessage) }
    }

    fun listenCards(uid: String, cb: (List<Card>) -> Unit): ListenerRegistration {
        return firestore.collection("users").document(uid).collection("cards")
            .addSnapshotListener { snap, e ->
                if (e != null || snap == null) {
                    cb(emptyList())
                    return@addSnapshotListener
                }

                val list = snap.documents.mapNotNull { doc ->
                    try {
                        val c = doc.toObject(Card::class.java)?.copy(id = doc.id)
                        c
                    } catch (_: Exception) {
                        null
                    }
                }
                cb(list)
            }
    }
}
