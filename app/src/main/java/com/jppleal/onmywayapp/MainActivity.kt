package com.jppleal.onmywayapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.jppleal.onmywayapp.ui.theme.OnMyWayAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            OnMyWayAppTheme {
                val isLoggedIn = isLoggedIn(this)
                // Set up your app's theme and content here
                OnMyWayApp(navController, isLoggedIn)
            }
        }
    }
}