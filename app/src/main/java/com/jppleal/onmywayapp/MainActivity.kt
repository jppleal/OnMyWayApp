package com.jppleal.onmywayapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.messaging.FirebaseMessaging
import com.jppleal.onmywayapp.ui.theme.OnMyWayAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val intent = Intent(this, AlertService::class.java)
            this.startService(intent)

            FirebaseMessaging.getInstance().subscribeToTopic("alert")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.e("FCM", "Subscribed to alert topic")
                    } else {
                        Log.e("FCM", "Failed to subscribe to alert topic", task.exception)
                    }
                }

            val navController = rememberNavController()
            OnMyWayAppTheme {
                OnMyWayApp(navController = navController, AuthFireB())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    RequestNotificationPermission()
                }
            }
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

