package com.jppleal.onmywayapp

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Auth  {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    fun loginUser(email: String, password: String, onComplete: (FirebaseUser?, String?) -> Unit){
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{task ->
                if(task.isSuccessful){
                    val user = firebaseAuth.currentUser
                    onComplete(user, null)
                }else{
                    onComplete(null, task.exception?.message)
                }
            }
    }
}

fun logOut(context: Context) {
    clearUserCredentials(context = context)
}

private fun clearUserCredentials(context: Context){
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().remove("userEmail").apply()
}