package com.jppleal.onmywayapp

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthFireB() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    //Login Function
    fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    val userId = currentUser?.uid
                    if (userId != null) {
                        SharedPrefsManager.setUserId(userId)
                        onSuccess()
                    }
                    else{
                        onFailure(Exception("Error getting user ID"))
                    }
                } else {
                    onFailure(task.exception!!)
                }
            }
    }

    //Regist function
    fun registerUser(
        email: String,
        password: String,
        name: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    val userId = currentUser?.uid
                    if(userId != null){
                        // salva informação do user no Realtime Database
                        val userData = mapOf(
                            "name" to name,
                            "email" to email
                        )
                        FirebaseDatabase.getInstance().getReference("users").child(userId)
                            .setValue(userData)
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                onFailure(e)
                            }
                    }else{
                        onFailure(Exception("Error saving user data"))
                    }
                } else {
                    onFailure(task.exception!!)
                }
            }
    }

    //isLogged function
    fun isLogged(): Boolean {
        SharedPrefsManager.setUserId(auth.currentUser?.uid ?: "")
        return auth.currentUser != null
    }

    //logout function
    fun logoutUser() {
        auth.signOut()
        //clean shared preferences
        SharedPrefsManager.clearUserId()
    }

    //get current user
    fun currentUser(): String? {
        return auth.currentUser?.uid
    }
}
