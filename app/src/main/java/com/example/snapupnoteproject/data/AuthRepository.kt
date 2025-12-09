package com.example.snapupnoteproject.data

import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class AuthRepository {
    private val auth = Firebase.auth

    fun currentUserUid(): String? = auth.currentUser?.uid

    fun register(email: String, password: String, cb: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) cb(true, null)
                else cb(false, task.exception?.localizedMessage)
            }
    }

    fun login(email: String, password: String, cb: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) cb(true, null)
                else cb(false, task.exception?.localizedMessage)
            }
    }

    fun logout() {
        auth.signOut()
    }
}
