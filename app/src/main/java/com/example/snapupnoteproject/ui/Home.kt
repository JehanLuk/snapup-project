package com.example.snapupnoteproject.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.snapupnoteproject.model.Card
import com.example.snapupnoteproject.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    val cards = viewModel.cards
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
                                Modifier.size(100.dp)
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
                                        imageVector = Icons.Default.Logout,
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
            IconButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier
                    .padding(16.dp)
                    .height(56.dp)
                    .width(56.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFF8CB414))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar card",
                    tint = Color.White
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            HorizontalDivider(modifier = Modifier.height(12.dp))

            if (cards.isEmpty()) {
                Text(
                    text = "Sua lista de lembranças está vazia!",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF5A5A5A)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cards, key = { it.id }) { card ->
                        CardRow(
                            card = card,
                            onDelete = { viewModel.deleteCard(card.id) }
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

@Composable
fun CardRow(card: Card, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(card.color)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(card.title.ifEmpty { "Sem título" }, style = MaterialTheme.typography.titleMedium)

            if (card.items.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    card.items.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            card.total?.let {
                Text(
                    text = "Total: R$ $it",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir"
                    )
                }
            }
        }
    }
}
