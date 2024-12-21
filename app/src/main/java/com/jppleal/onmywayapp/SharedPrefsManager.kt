package com.jppleal.onmywayapp

import android.content.Context
import android.content.SharedPreferences

object SharedPrefsManager {
    private const val PREFS_NAME = "UserPrefs"
    private lateinit var sharedPreferences: SharedPreferences
    // Initialize the SharedPreferences instance
    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    // Get the user ID
    fun getUserId():String ?{
        return sharedPreferences.getString("userId", null)
    }
    // Set the user ID
    fun setUserId(userId: String) {
        sharedPreferences.edit().putString("userId", userId).apply()
    }
    // Clear the user ID
    fun clearUserId() {
        sharedPreferences.edit().remove("userId").apply()
    }
}