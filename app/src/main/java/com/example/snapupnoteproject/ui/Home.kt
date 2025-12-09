package com.example.snapupnoteproject.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.snapupnoteproject.data.FirestoreRepository
import com.example.snapupnoteproject.data.AuthRepository
import com.example.snapupnoteproject.model.Card

@Composable
fun HomeScreen(onAddCard: (() -> Unit)? = null) {
    val authRepo = remember { AuthRepository() }
    val uid = authRepo.currentUserUid()
    val repo = remember { FirestoreRepository() }

    val cards = remember { mutableStateListOf<Card>() }
    var listenerRegistration = remember { null as? com.google.firebase.firestore.ListenerRegistration? }

    DisposableEffect(uid) {
        if (uid != null) {
            listenerRegistration = repo.listenCards(uid) { list ->
                cards.clear()
                cards.addAll(list)
            }
        }
        onDispose {
            listenerRegistration?.remove()
        }
    }

    Scaffold(
        floatingActionButton = {
            IconButton(
                onClick = { onAddCard?.invoke() },
                modifier = Modifier
                    .padding(16.dp)
                    .height(56.dp)
                    .width(56.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(140, 180, 20))
            ) {

            }
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Meus Cards",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            HorizontalDivider(
                thickness = DividerDefaults.Thickness, color = DividerDefaults.color
            )

            if (cards.isEmpty()) {
                Text(
                    text = "Sua lista de lembranças está vazia!",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(90, 90, 90)
                )
            } else {
                LazyColumn(modifier = Modifier.padding(8.dp)) {
                    items(cards) { card ->
                        CardRow(card)
                    }
                }
            }
        }
    }
}

@Composable
fun CardRow(card: Card) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = card.title.ifEmpty { "Sem título" }, style = MaterialTheme.typography.titleMedium)
            if (card.items.isNotEmpty()) {
                Text(text = card.items.joinToString(", "), style = MaterialTheme.typography.bodyMedium)
            }
            card.total?.let {
                Text(text = "Total: R$ $it", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
