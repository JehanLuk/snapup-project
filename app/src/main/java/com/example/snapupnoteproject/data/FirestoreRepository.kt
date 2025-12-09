package com.example.snapupnoteproject.data

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.example.snapupnoteproject.model.Card

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()

    private fun userCardsCollection(uid: String) =
        db.collection("users").document(uid).collection("cards")

    fun addCard(uid: String, card: Card, cb: (Boolean, String?) -> Unit) {
        val data = hashMapOf(
            "title" to card.title,
            "items" to card.items,
            "total" to card.total,
            "progress" to card.progress,
            "ownerId" to uid,
            "createdAt" to FieldValue.serverTimestamp()
        )
        userCardsCollection(uid).add(data)
            .addOnSuccessListener { docRef -> cb(true, docRef.id) }
            .addOnFailureListener { e -> cb(false, e.localizedMessage) }
    }

    fun updateCard(uid: String, cardId: String, updates: Map<String, Any>, cb: (Boolean, String?) -> Unit) {
        userCardsCollection(uid).document(cardId).update(updates)
            .addOnSuccessListener { cb(true, null) }
            .addOnFailureListener { e -> cb(false, e.localizedMessage) }
    }

    fun deleteCard(uid: String, cardId: String, cb: (Boolean, String?) -> Unit) {
        userCardsCollection(uid).document(cardId).delete()
            .addOnSuccessListener { cb(true, null) }
            .addOnFailureListener { e -> cb(false, e.localizedMessage) }
    }

    fun listenCards(uid: String, onChange: (List<Card>) -> Unit): ListenerRegistration {
        return userCardsCollection(uid)
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                val list = mutableListOf<Card>()
                snapshot?.documents?.forEach { doc ->
                    val c = Card(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        items = doc.get("items") as? List<String> ?: emptyList(),
                        total = doc.getDouble("total"),
                        progress = doc.getLong("progress")?.toInt(),
                        ownerId = doc.getString("ownerId") ?: "",
                        createdAt = doc.getTimestamp("createdAt")
                    )
                    list.add(c)
                }
                onChange(list)
            }
    }
}
