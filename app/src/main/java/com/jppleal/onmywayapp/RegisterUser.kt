package com.jppleal.onmywayapp

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterUser {
    fun registry(email: String, password: String, internalNumber: String, username: String, cbNumber: String, functions: List<String>, onComplete: (Boolean) -> Unit){
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{task ->
                if (task.isSuccessful){
                    val user = auth.currentUser
                    user?.let {
                        val userId = username
                        val userData= mapOf(
                            "internalNumber" to internalNumber,
                            "username" to username,
                            "email" to email,
                            "cbNumber"  to cbNumber,
                            "functions" to functions.joinToString(", ")
                        )
                        firestore.collection(cbNumber).document(userId).set(userData)
                            .addOnCompleteListener{dbTask ->
                                if (dbTask.isSuccessful){
                                    //handle successful registration and data saving
                                    Log.e("RegisterNewUser", "User data saved successfully for username $username")
                                    onComplete(dbTask.isSuccessful)
                                }
                                else{
                                    //Handle error and failures
                                    Log.e("RegisterNewUser", "Error saving user data: ${dbTask.exception?.message}")
                                }
                            }

                    }?: run {
                        Log.e("RegisterNewUser", "Failed to get current user after registration")
                    }
                }else{
                    Log.e("RegisterNewUser", "Failed registing the user: ${task.exception}")
                }
            }

    }
}