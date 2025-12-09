package com.example.snapupnoteproject.model

import com.google.firebase.Timestamp

data class Card(
    var id: String = "",
    var title: String = "",
    var items: List<String> = emptyList(),
    var total: Double? = null,
    var progress: Int? = null,
    var ownerId: String = "",
    var createdAt: Timestamp? = null
)
