package com.jppleal.onmywayapp

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.jppleal.onmywayapp.data.model.Alert


fun fetchNewAlertsFromBackend(alerts: SnapshotStateList<Alert>) {
    val database =
        FirebaseDatabase.getInstance("https://on-my-way-app-3ixreu-default-rtdb.europe-west1.firebasedatabase.app/")
    val alertsRef = database.getReference("alerts")

    // Listener para escutar atualizações em tempo real
        alertsRef.addChildEventListener(
        object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val newAlert = snapshot.getValue(Alert::class.java)
                newAlert?.let {
                    alerts.add(it)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val updatedAlert = snapshot.getValue(Alert::class.java)
                updatedAlert?.let { alert ->
                    val index = alerts.indexOfFirst { it.id == alert.id }
                    if (index != -1) {
                        alerts[index] = alert
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val removedAlert = snapshot.getValue(Alert::class.java)
                removedAlert?.let { alert ->
                    alerts.remove(alert)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Alert.kt", "Erro ao escutar alertas: ${error.message}")
            }
        })
}

fun updatedAlertResponse(alertId: Int, status: String, estimatedTime: Int? = null) {
    val database =
        FirebaseDatabase.getInstance("https://on-my-way-app-3ixreu-default-rtdb.europe-west1.firebasedatabase.app/")
    val alertsRef = database.getReference("alerts").child(alertId.toString())

    //creates map of updates
    val updates = hashMapOf<String, Any>(
        "status" to status
    )
    estimatedTime?.let {
        updates["estimatedTime"] = it
    }

    //updates the data of the alert in the Firebase database
    alertsRef.updateChildren(updates)
        .addOnSuccessListener {
            Log.d("AlertResponse", "Resposta atualizada com sucesso.")
        }
        .addOnFailureListener {
            Log.e("AlertResponse", "Erro ao atualizar resposta: ${it.message}")
        }
}

fun addAlertToFirebase(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    //get firebaseDatabase instance
    val database =
        FirebaseDatabase.getInstance("https://on-my-way-app-3ixreu-default-rtdb.europe-west1.firebasedatabase.app/")
    //obtain a reference for the 'alerts' knot
    val alertsRef = database.getReference("alerts")

    //create a unique ID for a new alert
    val alertId = alertsRef.push().key ?: return

    //Data from the alert to be added
    val alertData = Alert(
        id = alertId.hashCode(),
        message = "Inc. Urbano",
        dateTime = System.currentTimeMillis(),
        firefighters = 4,
        graduated = 1,
        truckDriver = 1
    )

    //verifying if the alertId is not null
    if (alertId != null) {
        alertsRef.child(alertId).setValue(alertData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                    Log.d("AlertSent", "New alert sent with success.")
                } else {
                    onFailure(Exception("Error sending new alert."))
                    Log.e("AlertSent", "New alert failed to sent.")
                }
            }
    } else {
        Log.e("AlertSent", "Error: alertId is null.")
    }


}