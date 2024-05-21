package com.jppleal.onmywayapp

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterUser {
    fun registry(email: String, password: String, internalNumber: String, username: String, cbNumber: String, functions: List<String>){
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{task ->
                if (task.isSuccessful){
                    val user = auth.currentUser
                    user?.let {
                        val userId = it.uid
                        val userData= mapOf(
                            "internalNumber" to internalNumber,
                            "username" to username,
                            "email" to email,
                            "cbNumber"  to cbNumber,
                            "functions" to functions.associateWith{true}
                        )
                        firestore.collection("users").document(userId).set(userData)
                            .addOnCompleteListener{dbTask ->
                                if (dbTask.isSuccessful){
                                    //handle successful registration and data saving
                                    Log.e("RegisterNewUser", "username: $username" )
                                    Log.e("RegisterNewUser", "User data saved successfully")
                                }
                                else{
                                    //Handle error and failures
                                    Log.e("RegisterNewUser", "Error saving user data: ${dbTask.exception}")
                                }
                            }

                    }
                }else{
                    Log.e("RegisterNewUser", "Failed registing the user: ${task.exception}")
                }
            }

    }
}