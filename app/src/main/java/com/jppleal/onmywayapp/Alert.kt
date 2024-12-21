package com.jppleal.onmywayapp

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.jppleal.onmywayapp.data.model.Alert
import com.jppleal.onmywayapp.data.model.Response


fun fetchNewAlertsFromBackend(alerts: SnapshotStateList<Alert>) {
    val database =
        FirebaseDatabase.getInstance("https://on-my-way-app-3ixreu-default-rtdb.europe-west1.firebasedatabase.app/")
    val alertsRef = database.getReference("alerts")

    // Listener para escutar atualizações em tempo real
    alertsRef.addChildEventListener(object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val newAlert = snapshot.getValue(Alert::class.java)
            if (newAlert != null) {
                alerts.add(newAlert)
                Log.d("Alert.kt", "Novo alerta adicionado: $newAlert")
            } else {

                Log.d("Alert.kt", "Alerta já existente: $newAlert")
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            val updatedAlert = snapshot.getValue(Alert::class.java)
            if (updatedAlert != null) {
                val index = alerts.indexOfFirst { it.firebaseKey == updatedAlert.firebaseKey }
                if (index >= 0) {
                    alerts[index] = updatedAlert
                    Log.d("Alert.kt", "Alerta atualizado: $updatedAlert")
                }
            }
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            val removedAlert = snapshot.getValue(Alert::class.java)
            if (removedAlert != null) {
                alerts.removeIf { it.firebaseKey == removedAlert.firebaseKey }
                Log.d("Alert.kt", "Alerta removido: $removedAlert")
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

fun updatedAlertResponse(alert: Alert, status: Boolean, estimatedTime: Int? = null) {
    // Get UID from shared preferences
    val userId = SharedPrefsManager.getUserId().toString()
    val database = FirebaseDatabase.getInstance("https://on-my-way-app-3ixreu-default-rtdb.europe-west1.firebasedatabase.app/")
    val alertsRef = database.getReference("alerts").child(alert.firebaseKey)
    val usersRef = database.getReference("users").child(userId) // Referência ao nó de utilizadores

    // Busca o nome do utilizador
    usersRef.get().addOnSuccessListener { snapshot ->
        val userName = snapshot.child("name").value as? String ?: "Desconhecido"

        // Cria o mapa de atualizações
        val updates = hashMapOf<String, Any>(
            "status" to status,
            "name" to userName // Adiciona o nome do utilizador
        )
        estimatedTime?.let {
            updates["estimatedTime"] = it
        }

        // Atualiza os dados do alerta no Firebase Database
        alertsRef.child("responded").child(userId).setValue(updates)
            .addOnSuccessListener {
                Log.d("Alert.kt", "Resposta atualizada com sucesso.")
            }
            .addOnFailureListener {
                Log.e("Alert.kt", "Erro ao atualizar resposta: ${it.message}")
            }
    }.addOnFailureListener { exception ->
        Log.e("Alert.kt", "Erro ao obter o nome do utilizador: ${exception.message}")
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
        firebaseKey = alertId,
        id = alertId.hashCode(),
        message = "Inc. Urbano",
        dateTime = System.currentTimeMillis(),
        firefighters = 4,
        graduated = 1,
        truckDriver = 1
    )
    //verifying if the alertId is not null
    alertsRef.child(alertId).setValue(alertData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess()
                Log.d("Alert.kt", "New alert sent with success.")
            } else {
                onFailure(Exception("Error sending new alert."))
                Log.e("Alert.kt", "New alert failed to sent.")
            }
        }
}

//fun hasUserResponded(alert: Alert): Boolean {
//    val userId = SharedPrefsManager.getUserId().toString()
//    return alert.responded?.containsKey(userId) == true
//
//}

fun fetchUserResponse(alert: Alert, onResult: (Response?) -> Unit){
    val userId = SharedPrefsManager.getUserId().toString()
    val database =
        FirebaseDatabase.getInstance("https://on-my-way-app-3ixreu-default-rtdb.europe-west1.firebasedatabase.app/")
    val alertRef = database.getReference("alerts").child(alert.firebaseKey).child("responded").child(userId)
    alertRef.get().addOnSuccessListener { snapshot ->
        val response= snapshot.getValue(Response::class.java)
        onResult(response)
    }.addOnFailureListener { exception ->
        Log.e("Alert.kt", "Erro ao carregar a resposta: ${exception.message}")
        onResult(null)
    }
}