package com.jppleal.onmywayapp

import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.firestore.FirebaseFirestore
import com.jppleal.onmywayapp.NotificationUtils.showNotification
import com.jppleal.onmywayapp.data.model.Alert

class FirestoreListener (
    private val db: FirebaseFirestore
){
    companion object {
        private val TAG = "FirestoreListener.kt"
    }
    internal fun runAll(context: Context){
        Log.d(TAG, "========= RUNNING FirestoreListener.kt =========")

        listenToDocument(context)

    }

    //Listen to firestore document
    private fun listenToDocument(context: Context) {
        val docRef = db.collection("Alerts") //.document("Alert")
        docRef.addSnapshotListener{ snapshot, e ->
            if (e != null){
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null){
                for (document in snapshot.documents){
                    val alert = document.toObject(Alert::class.java)
                    if (alert != null){
                        val notificationId = alert.dateTime.toInt()
                        val title = alert.message
                        val message = messageInterpreter() /*TODO: fun messageInterpreter() */
                        showNotification(context = context, title, message, notificationId)
                    }
                    Log.d(TAG, "Document ID: ${document.id}")
                    Log.d(TAG, "Document Data: ${document.data}")
                }
                //Log.d(TAG, "Current data: ${snapshot.data}")
            }else{
                Log.d(TAG, "Current data: null")
            }
        }
    }

    //Listen local document (on the user device)
    private fun listenToDocumentLocal(){
        val docRef = db.collection("Alerts").document()
        val registration = docRef.addSnapshotListener{ snapshot, e ->
            if(e != null){
                Log.w(TAG, "Listen local doc failed.", e)
                return@addSnapshotListener
            }

            val source = if (snapshot != null && snapshot.metadata.hasPendingWrites()){
                "Local"
            }else{
                "Server"
            }

            if(snapshot != null && snapshot.exists()){
                Log.d(TAG, "$source data: ${snapshot.data}")
            }else  {
                Log.d(TAG, "$source data: null")
            }
        }

        registration.remove() //stop the local listener after being activated
    }

}