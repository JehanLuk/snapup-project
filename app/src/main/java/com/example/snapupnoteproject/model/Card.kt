package com.example.snapupnoteproject.model

import com.google.firebase.Timestamp

data class Card(
    val id: String = "",
    val title: String = "",
    val items: List<String> = emptyList(),
    val total: Double? = null,
    val progress: Int? = null,
    val color: Int = 0xFFF6F6C9.toInt(),
    val ownerId: String = "",
    val createdAt: Timestamp? = null
)