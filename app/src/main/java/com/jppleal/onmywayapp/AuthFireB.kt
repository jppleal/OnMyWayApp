package com.jppleal.onmywayapp

import com.google.firebase.auth.FirebaseAuth

class AuthFireB()  {
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

    //isLogged function
    fun isLogged(): Boolean {
        return auth.currentUser != null
    }

    //logout function
    fun logoutUser(){
        auth.signOut()
    }
}
