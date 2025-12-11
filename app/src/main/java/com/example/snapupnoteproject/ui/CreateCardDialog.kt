package com.example.snapupnoteproject.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.snapupnoteproject.data.FirestoreRepository
import com.example.snapupnoteproject.model.Card
import com.example.snapupnoteproject.model.Item

@Composable
fun CreateCardDialog(
    uid: String,
    onDismiss: () -> Unit,
    onSaved: (createdId: String) -> Unit = {}
) {
    val repo = FirestoreRepository()

    var title by remember { mutableStateOf("") }
    var items by remember { mutableStateOf(listOf(Item(text = ""))) }
    var totalText by remember { mutableStateOf("") }
    var selectedColor by remember { mutableIntStateOf(0xFFFFF59D.toInt()) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val palette = listOf(
        0xFFFFF59D.toInt(),
        0xFFA5D6A7.toInt(),
        0xFFEF9A9A.toInt(),
        0xFF90CAF9.toInt(),
        0xFFFFCC80.toInt(),
        0xFFB2DFDB.toInt()
    )

    AlertDialog(
        onDismissRequest = { if (!loading) onDismiss() },
        confirmButton = {
            TextButton(
                onClick = {
                    val filtered = items.map { it.text.trim() }.filter { it.isNotEmpty() }
                    if (title.isBlank() && filtered.isEmpty()) {
                        error = "Preencha título ou pelo menos 1 item"
                        return@TextButton
                    }
                    loading = true
                    val itemsToSave = filtered.map { Item(text = it, done = false) }
                    val total = totalText.trim().ifEmpty { null }?.replace(",", ".")?.toDoubleOrNull()
                    val card = Card(
                        title = title.trim(),
                        items = itemsToSave,
                        total = total,
                        color = selectedColor,
                        ownerId = uid,
                        pinned = false
                    )
                    repo.addCard(uid, card) { success, idOrMsg ->
                        loading = false
                        if (success) {
                            onSaved(idOrMsg ?: "")
                            onDismiss()
                        } else {
                            error = idOrMsg ?: "Erro ao salvar"
                        }
                    }
                },
                enabled = !loading
            ) {
                Text(if (loading) "Salvando..." else "Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = { if (!loading) onDismiss() }) { Text("Cancelar") }
        },
        title = {
            Text(text = "Criar novo card", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )

                Text("Itens", style = MaterialTheme.typography.labelMedium)
                Column(modifier = Modifier.fillMaxWidth()) {
                    items.forEachIndexed { index, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            OutlinedTextField(
                                value = item.text,
                                onValueChange = { new ->
                                    items = items.toMutableList().also { mut -> mut[index] = Item(new) }
                                },
                                label = { Text("Item ${index + 1}") },
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(onClick = {
                                val mutable = items.toMutableList()
                                if (mutable.size > 1) {
                                    mutable.removeAt(index)
                                    items = mutable.toList()
                                } else {
                                    items = listOf(Item(text = ""))
                                }
                            }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Remover")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    TextButton(onClick = {
                        items = items + Item(text = "")
                    }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Adicionar")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Adicionar item")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = totalText,
                    onValueChange = { totalText = it },
                    label = { Text("Total (opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Cor do card", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 6.dp)
                ) {
                    palette.forEach { col ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(col))
                                .border(
                                    width = if (selectedColor == col) 2.dp else 0.dp,
                                    color = if (selectedColor == col) Color.Black else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = col }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }

                error?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    )
}