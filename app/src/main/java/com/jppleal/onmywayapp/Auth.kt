package com.jppleal.onmywayapp

import android.content.Context

fun loginUser(email: String, password: String, context: Context): Int? {
    if(userPasswords != null && userPasswords[email] == password){
        val user = users.find { it.email == email } ?: return null
        saveUserCredentials(email = email, context)
        return user.internalNumber
    }
    return null
}

// Function to check if user is logged in
fun isLoggedIn(context: Context): Boolean {
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    return sharedPreferences.getString("userEmail", null) != null
}

// Function to save user credentials
private fun saveUserCredentials(email: String, context: Context) {
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("userEmail", email).apply()
}

fun logOut(context: Context) {
    clearUserCredentials(context = context)
}

private fun clearUserCredentials(context: Context){
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().remove("userEmail").apply()
}