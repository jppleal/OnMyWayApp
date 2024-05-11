package com.jppleal.onmywayapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
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
            FirebaseApp.initializeApp(this);
            OnMyWayAppTheme {
                /*TODO: Check for permission for notifications*/
                firebaseRef = FirebaseDatabase.getInstance("https://on-my-way-app-3ixreu-default-rtdb.europe-west1.firebasedatabase.app/").getReference("test")
                firebaseRef.setValue("Test dude").addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Firebase", "Data sent successfully")
                    } else {
                        Log.e("Firebase", "Failed to send data: ${task.exception}")
                    }
                }
                val isLoggedIn = isLoggedIn(this)
                // Set up your app's theme and content here
                OnMyWayApp(navController, isLoggedIn)
            }
        }
    }
}

