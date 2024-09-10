package com.jppleal.onmywayapp

import android.util.Log
import com.google.firebase.database.FirebaseDatabase

fun addAlertToFirebase(){
    //get firebaseDatabase instance
    val database = FirebaseDatabase.getInstance("https://on-my-way-app-3ixreu-default-rtdb.europe-west1.firebasedatabase.app/")
    //obtain a reference for the 'alerts' knot
    val alertsRef = database.getReference("alerts")

    alertsRef.setValue("Testing")
        .addOnSuccessListener {
            Log.d("newalert","Working")
        }
        .addOnFailureListener {
            Log.d("newalert", "Not working")
        }
    //create a unique ID for a new alert
    val alertId = alertsRef.push().key

    //Data from the alert to be added
    val alertData = mapOf(
        "message" to "This an alert for test!",
        "timestamp" to System.currentTimeMillis()
    )

    //verifyin if the alertId is not null
    if(alertId != null){
        alertsRef.child(alertId).setValue(alertData)
            .addOnSuccessListener {
                Log.d("NewAlert", "New alert sent with success.")
            }
            .addOnFailureListener{
                Log.e("NewAlert", "New alert failed to sent.")
            }
    }else{
        Log.e("NewAlert", "Error: alertId is null.")
    }
}