package com.jppleal.onmywayapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jppleal.onmywayapp.ui.theme.OnMyWayAppTheme

class MainActivity : ComponentActivity() {

    private lateinit var firebaseRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NotificationUtils.createNotificationChannel(this)
            OnMyWayAppTheme {
                /*TODO: Check for permission for notifications*/
                firebaseRef = FirebaseDatabase.getInstance().getReference("test")
                firebaseRef.setValue("Test dude")
                val isLoggedIn = isLoggedIn(this)
                // Set up your app's theme and content here
                OnMyWayApp(navController, isLoggedIn)
            }
        }
    }
}