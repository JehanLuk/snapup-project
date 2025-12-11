package com.example.snapupnoteproject.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.snapupnoteproject.data.AuthRepository

class LoginViewModel(
    private val authRepo: AuthRepository = AuthRepository()
) : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var loading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var loginSuccess by mutableStateOf(false)

    fun login() {
        if (email.isBlank() || password.isBlank()) {
            error = "Preencha email e senha"
            return
        }

        loading = true
        error = null

        authRepo.login(email, password) { success, message ->
            loading = false
            if (success) {
                loginSuccess = true
            } else {
                error = message ?: "Email ou senha incorretos"
            }
        }
    }

    fun logout() {
        authRepo.logout()
        loginSuccess = false
    }
}
