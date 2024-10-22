package com.jppleal.onmywayapp

import android.Manifest
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
import com.google.firebase.auth.FirebaseAuth
import com.jppleal.onmywayapp.ui.theme.OnMyWayAppTheme
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

class MainActivity : ComponentActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    val client = createSupabaseClient(
        supabaseUrl = "https://qhhsqjvlgkdszjbsawyi.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFoaHNxanZsZ2tkc3pqYnNhd3lpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mjk1MzU5NTUsImV4cCI6MjA0NTExMTk1NX0.aRE0wZbkIpRxJsRps3Hwk1XRml5lyMNZyOQzX26FGcw"
    ){
        install(Postgrest)
        install(Realtime)
        install(Auth)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val session = client.auth.currentSessionOrNull()
            Log.e("CheckSession", session.toString())
            OnMyWayAppTheme {
                OnMyWayApp(navController = navController, isLoggedIn = session)
                Log.e("CheckSessionIsNull", session.toString())

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

