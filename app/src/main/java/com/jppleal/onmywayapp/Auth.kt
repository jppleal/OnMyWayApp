package com.jppleal.onmywayapp

import android.content.Context
import androidx.activity.ComponentActivity

fun loginUser(email: String, password: String): Int? {
    if(userPasswords != null && userPasswords[email] == password){
        val user = users.find { it.email == email } ?: return null
        return user.internalNumber

    }
    return null
}

private fun logOut(context: Context) {
    val sharePref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val editor = sharePref.edit()
    editor.remove("userEmail")
    editor.apply()

    (context as ComponentActivity).recreate()
}