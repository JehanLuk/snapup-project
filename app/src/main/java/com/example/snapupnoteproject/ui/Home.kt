package com.example.snapupnoteproject.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.snapupnoteproject.model.Card
import com.example.snapupnoteproject.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    val cardsUnsorted = viewModel.cards
    val cards = remember(cardsUnsorted.toList()) {
        cardsUnsorted.sortedWith(compareByDescending<Card> { it.pinned }
            .thenByDescending { it.createdAt?.seconds ?: 0L })
    }

    var showCreateDialog by remember { mutableStateOf(false) }
    var showProfileMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Meus Cards",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    Box {
                        IconButton(onClick = { showProfileMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Perfil",
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = showProfileMenu,
                            onDismissRequest = { showProfileMenu = false },
                            offset = DpOffset(x = 0.dp, y = 20.dp)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Sair") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Logout,
                                        contentDescription = "Sair"
                                    )
                                },
                                onClick = {
                                    showProfileMenu = false
                                    viewModel.logout()
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = Color(147, 49, 204)
            ) {
                Icon(imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar",
                    tint = Color.White)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(),
                thickness = 1.dp,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.size(8.dp))
            if (cards.isEmpty()) {
                Text(
                    text = "Sua lista está vazia.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF5A5A5A)
                )
            } else {
                androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                    columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cards, key = { it.id }) { card ->
                        CardRow(
                            card = card,
                            onDelete = { viewModel.deleteCard(card.id) },
                            onToggleItem = { cardId, idx -> viewModel.toggleItemDone(cardId, idx) },
                            onTogglePin = { cardId -> viewModel.togglePin(cardId) }
                        )
                    }
                }
            }
        }

        if (showCreateDialog && viewModel.currentUid != null) {
            CreateCardDialog(
                uid = viewModel.currentUid!!,
                onDismiss = { showCreateDialog = false },
                onSaved = { _ -> showCreateDialog = false }
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun CardRow(
    card: Card,
    onDelete: () -> Unit,
    onToggleItem: (String, Int) -> Unit = { _, _ -> },
    onTogglePin: (String) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(containerColor = Color(card.color)),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = card.title.ifEmpty { "Sem título" },
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = { onTogglePin(card.id) },
                    modifier = Modifier.size(36.dp)
                ) {
                    if (card.pinned) {
                        Icon(imageVector = Icons.Default.Bookmark, contentDescription = "Pinned")
                    } else {
                        Icon(imageVector = Icons.Outlined.BookmarkBorder, contentDescription = "Pin")
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            if (card.items.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 120.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    card.items.forEachIndexed { index, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Checkbox(
                                checked = item.done,
                                onCheckedChange = { _ -> onToggleItem(card.id, index) },
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item.text,
                                style = MaterialTheme.typography.bodyMedium,
                                softWrap = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))

                card.total?.let {
                    Text(
                        text = "Total: R$ ${String.format("%.2f", it)}",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Excluir")
                }
            }
        }
    }
}
