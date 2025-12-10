package com.example.snapupnoteproject.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.snapupnoteproject.data.FirestoreRepository
import com.example.snapupnoteproject.model.Card

@Composable
fun CreateCardDialog(
    uid: String,
    onDismiss: () -> Unit,
    onSaved: (createdId: String) -> Unit = {}
) {
    val repo = remember { FirestoreRepository() }

    var title by remember { mutableStateOf("") }
    var itemsText by remember { mutableStateOf("") }
    var totalText by remember { mutableStateOf("") }
    var selectedColor by remember { mutableIntStateOf(0xFFF6F6C9.toInt()) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        selectedColor = listOf(
            0xFFFFF59D.toInt(), 0xFFA5D6A7.toInt(), 0xFFEF9A9A.toInt(),
            0xFF90CAF9.toInt(), 0xFFFFCC80.toInt(), 0xFFD7BDE2.toInt()
        ).random()
    }

    AlertDialog(
        onDismissRequest = { if (!loading) onDismiss() },
        confirmButton = {
            Button(onClick = {
                if (title.isBlank() && itemsText.isBlank()) {
                    error = "Preencha título ou itens"
                    return@Button
                }
                loading = true
                val items = itemsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                val total = totalText.trim().ifEmpty { null }?.replace(",",".")?.toDoubleOrNull()
                val card = Card(
                    title = title.trim(),
                    items = items,
                    total = total,
                    color = selectedColor,
                    ownerId = uid
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
            }) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = { if (!loading) onDismiss() }) { Text("Cancelar") }
        },
        title = { Text("Criar novo card") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                OutlinedTextField(value = itemsText, onValueChange = { itemsText = it }, label = { Text("Itens (separados por vírgula)") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                OutlinedTextField(value = totalText, onValueChange = { totalText = it }, label = { Text("Total (opcional)") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                error?.let { Text(it, color = androidx.compose.ui.graphics.Color.Red) }
            }
        }
    )
}
