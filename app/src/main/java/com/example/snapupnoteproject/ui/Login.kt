package com.example.snapupnoteproject.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.snapupnoteproject.data.AuthRepository

private const val TAG = "LoginScreen"

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val repo = remember { AuthRepository() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login")

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha") },
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        if (error.isNotEmpty()) {
            Text(error, color = Color.Red, modifier = Modifier.padding(8.dp))
        }

        if (loading) {
            CircularProgressIndicator(modifier = Modifier.padding(8.dp))
        }

        Button(
            onClick = {
                error = ""
                if (email.isBlank() || password.isBlank()) {
                    error = "Preencha email e senha"
                    return@Button
                }

                loading = true
                try {
                    repo.login(email.trim(), password) { success, message ->
                        loading = false
                        if (success) {
                            onLoginSuccess()
                        } else {
                            Log.e(TAG, "Login failed: $message")
                            error = message ?: "Erro ao autenticar. Verifique suas credenciais."
                        }
                    }
                } catch (ex: Exception) {
                    loading = false
                    Log.e(TAG, "Exception during login", ex)
                    error = "Erro interno: ${ex.localizedMessage ?: "verifique logs"}"
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Entrar")
        }
    }
}
