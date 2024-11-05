package com.jppleal.onmywayapp

import android.content.Context
import com.google.firebase.auth.FirebaseAuth

class AuthFireB(private val context: Context)  {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    //Login Function
    fun loginUser(email: String, password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(task.exception!!)
                }
            }
    }

    //Regist function
    fun registerUser(email: String, password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(task.exception!!)
                }
            }
    }
}
