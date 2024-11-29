package com.jppleal.onmywayapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jppleal.onmywayapp.data.model.Alert

class AlertService : Service() {
    private lateinit var databaseRef: DatabaseReference
    private val listener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Handle the data change event
            for (alertSnapshot in dataSnapshot.children) {
                val alert = alertSnapshot.getValue(Alert::class.java)
                // Process the alert data as needed
                alert?.let {
                    NotificationUtils.showNotification(
                        applicationContext, "Nova Ocorrência!", it.message, it.dateTime.toInt()
                    )
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.e("AlertService.kt", "Error listening the alerts: ${error.message}")
        }
    }

    override fun onCreate() {
        super.onCreate()
        databaseRef =
            FirebaseDatabase.getInstance("https://on-my-way-app-3ixreu-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("alerts")
        databaseRef.addValueEventListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        //removes the listener when the service is destroyed
        databaseRef.removeEventListener(listener)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null //it's not necessary for this service
    }

}