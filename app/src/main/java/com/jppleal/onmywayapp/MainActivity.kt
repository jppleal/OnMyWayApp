package com.jppleal.onmywayapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jppleal.onmywayapp.ui.theme.OnMyWayAppTheme

class MainActivity : ComponentActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            firebaseAuth = FirebaseAuth.getInstance()
            NotificationUtils.createNotificationChannel(this)
            FirebaseApp.initializeApp(this);
            OnMyWayAppTheme {
                /*TODO: Check for permission for notifications*/
                //Set up your app's theme and content here
                OnMyWayApp(navController, firebaseAuth.currentUser != null)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    RequestNotificationPermission()
                }
            }
            FirestoreListener(FirebaseFirestore.getInstance()).runAll(this)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    fun RequestNotificationPermission(){
        val permissionState = remember { mutableStateOf(false)}

        val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
            isGranted:Boolean ->
            permissionState.value = isGranted
        }
        LaunchedEffect(Unit) {
            if(ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        if(!permissionState.value){
            /*TODO: Show some UI explanation on why notifications permission is needed*/
        }
    }
}

