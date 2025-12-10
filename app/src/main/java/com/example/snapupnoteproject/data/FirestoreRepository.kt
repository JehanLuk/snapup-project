package com.example.snapupnoteproject.data

import com.example.snapupnoteproject.model.Card
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.DocumentSnapshot

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()

    private fun userCardsCollection(uid: String) =
        db.collection("users").document(uid).collection("cards")

    fun addCard(uid: String, card: Card, cb: (Boolean, String?) -> Unit = { _, _ -> }) {
        val docRef = userCardsCollection(uid).document()
        val data = hashMapOf(
            "id" to docRef.id,
            "title" to card.title,
            "items" to card.items,
            "total" to card.total,
            "progress" to card.progress,
            "color" to card.color,
            "ownerId" to uid,
            "createdAt" to FieldValue.serverTimestamp()
        )
        docRef.set(data)
            .addOnSuccessListener { cb(true, docRef.id) }
            .addOnFailureListener { e -> cb(false, e.localizedMessage) }
    }

    fun deleteCard(uid: String, cardId: String, cb: (Boolean, String?) -> Unit = { _, _ -> }) {
        userCardsCollection(uid).document(cardId).delete()
            .addOnSuccessListener { cb(true, null) }
            .addOnFailureListener { e -> cb(false, e.localizedMessage) }
    }

    fun listenCards(uid: String, onChange: (List<Card>) -> Unit): ListenerRegistration {
        return userCardsCollection(uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onChange(emptyList())
                    return@addSnapshotListener
                }
                val list = mutableListOf<Card>()
                snapshot?.documents?.forEach { doc ->
                    list.add(documentToCard(doc))
                }
                onChange(list)
            }
    }

    private fun documentToCard(doc: DocumentSnapshot): Card {
        val id = doc.id
        val title = doc.getString("title") ?: ""
        val items = (doc.get("items") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
        val total = doc.getDouble("total")
        val progress = (doc.getLong("progress")?.toInt()) ?: doc.getDouble("progress")?.toInt()
        val rawColor = doc.get("color")
        val colorInt = when (rawColor) {
            is Long -> rawColor.toInt()
            is Double -> rawColor.toInt()
            is Int -> rawColor
            else -> 0xFFF6F6C9.toInt()
        }
        val ownerId = doc.getString("ownerId") ?: ""
        val createdAt = doc.getTimestamp("createdAt")
        return Card(
            id = id,
            title = title,
            items = items,
            total = total,
            progress = progress,
            color = colorInt,
            ownerId = ownerId,
            createdAt = createdAt
        )
    }
}
