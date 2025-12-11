package com.example.snapupnoteproject.model

import com.google.firebase.Timestamp

data class Item(
    val text: String = "",
    val done: Boolean = false
)

data class Card(
    val id: String = "",
    val title: String = "",
    val items: List<Item> = emptyList(),
    val total: Double? = null,
    val color: Int = 0xFFF6F6C9.toInt(),
    val ownerId: String = "",
    val pinned: Boolean = false,
    val createdAt: Timestamp? = null
)