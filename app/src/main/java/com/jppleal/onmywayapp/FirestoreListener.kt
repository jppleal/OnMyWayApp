package com.jppleal.onmywayapp

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreListener (
    private val db: FirebaseFirestore
){
    companion object {
        private val TAG = "FirestoreListener.kt"
    }
    internal fun runAll(){
        Log.d(TAG, "========= RUNNING FirestoreListener.kt =========")

        listenToDocument()

    }

    //Listen to firestore document
    private fun listenToDocument() {
        val docRef = db.collection("Alerts") //.document("Alert")
        docRef.addSnapshotListener{ snapshot, e ->
            if (e != null){
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null){
                for (document in snapshot.documents){
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