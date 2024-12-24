package com.jppleal.onmywayapp

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthFireB {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Login Function
    fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val sanitizedEmail = email.trim()
        val sanitizedPassword = password.trim()

        auth.signInWithEmailAndPassword(sanitizedEmail, sanitizedPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    val userId = currentUser?.uid
                    if (userId != null) {
                        SharedPrefsManager.setUserId(userId)
                        onSuccess()
                    } else {
                        onFailure(Exception("Erro ao obter UID do utilizador"))
                    }
                } else {
                    task.exception?.let {
                        onFailure(it)
                    } ?: onFailure(Exception("Erro desconhecido durante o login"))
                }
            }
    }

    // Regist Function
    fun registerUser(
        email: String,
        password: String,
        name: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val sanitizedEmail = email.trim()
        val sanitizedPassword = password.trim()

        auth.createUserWithEmailAndPassword(sanitizedEmail, sanitizedPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    val userId = currentUser?.uid
                    if (userId != null) {
                        // Salva informação do utilizador no Realtime Database
                        val userData = mapOf(
                            "name" to name,
                            "email" to sanitizedEmail
                        )
                        FirebaseDatabase.getInstance().getReference("users").child(userId)
                            .setValue(userData)
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                onFailure(e)
                            }
                    } else {
                        onFailure(Exception("Erro ao salvar dados do utilizador"))
                    }
                } else {
                    task.exception?.let {
                        onFailure(it)
                    } ?: onFailure(Exception("Erro desconhecido durante o registo"))
                }
            }
    }

    // Check if User is Logged In
    fun isLogged(): Boolean {
        val currentUserId = auth.currentUser?.uid
        SharedPrefsManager.setUserId(currentUserId ?: "")
        return currentUserId != null
    }

    // Logout Function
    fun logoutUser() {
        auth.signOut()
        // Limpa Shared Preferences
        SharedPrefsManager.clearUserId()
    }

    // Get Current User ID
    fun currentUser(): String? {
        return auth.currentUser?.uid
    }
}
