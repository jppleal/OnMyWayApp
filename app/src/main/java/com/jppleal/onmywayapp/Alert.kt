package com.jppleal.onmywayapp

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class Alert(
    val id: String = "",
    val message: String = "",
    val dateTime: Long = 0
)

fun fetchNewAlertsFromBackend(alerts: SnapshotStateList<Alert>) {
    val database = FirebaseDatabase.getInstance("https://on-my-way-app-3ixreu-default-rtdb.europe-west1.firebasedatabase.app/")
    val alertsRef = database.getReference("alerts")

    // Listener para escutar atualizações em tempo real
    alertsRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val newAlerts = mutableListOf<Alert>()
            for (alertSnapshot in snapshot.children) {
                val alert = alertSnapshot.getValue(Alert::class.java)
                if (alert != null) {
                    newAlerts.add(alert)
                }
            }
            // Atualiza a lista de alertas de maneira reativa
            alerts.clear()
            alerts.addAll(newAlerts)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("AlertListener", "Erro ao escutar alertas: ${error.message}")
        }
    })
}

//suspend fun fetchNewAlertsFromBackend(): List<Alert> = suspendCoroutine{ continuation ->
//    val db =
//        FirebaseDatabase.getInstance("https://on-my-way-app-3ixreu-default-rtdb.europe-west1.firebasedatabase.app/")
//    val alertsRef = db.getReference("alerts")
//
//    alertsRef.addListenerForSingleValueEvent(object : ValueEventListener {
//        override fun onDataChange(snapshot: DataSnapshot) {
//            val alertsList = mutableListOf<Alert>()
//            for (alertSnapshot in snapshot.children) {
//                val alert = alertSnapshot.getValue(Alert::class.java)
//                alert?.let { alertsList.add(it) }
//            }
//            continuation.resume(alertsList)
//        }
//
//
//        override fun onCancelled(error: DatabaseError) {
//            continuation.resumeWithException(error.toException())
//            Log.e("fetchNewAlerts", "Error fetching new alerts: ${error.message}")
//        }
//    })
//}

fun addAlertToFirebase(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    //get firebaseDatabase instance
    val database =
        FirebaseDatabase.getInstance("https://on-my-way-app-3ixreu-default-rtdb.europe-west1.firebasedatabase.app/")
    //obtain a reference for the 'alerts' knot
    val alertsRef = database.getReference("alerts")

    //create a unique ID for a new alert
    val alertId = alertsRef.push().key

    //Data from the alert to be added
    val alertData = mapOf(
        "message" to "This an alert for test!",
        "timestamp" to System.currentTimeMillis()
    )

    //verifyin if the alertId is not null
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