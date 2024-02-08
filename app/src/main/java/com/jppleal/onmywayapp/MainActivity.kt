package com.jppleal.onmywayapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            AppContent()
        }
    }
}

@Composable
fun AppContent(){
    var loggedIn by remember { mutableStateOf(false)}
    if (loggedIn) {
        HomeScreen()
    }else{
        LoginScreen{loggedIn = it}
    }
}

@Composable
fun LoginScreen(onLoginSuccess: (Boolean) -> Unit) {
    var cbNumber by remember { mutableStateOf("") }
    var internalNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.jose_logo_01),
                contentDescription = "Logo",
                modifier = Modifier.size(300.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = cbNumber,
                onValueChange = { cbNumber = it },
                label = { Text(text = "Nº CB") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = internalNumber,
                onValueChange = { internalNumber = it },
                label = { Text(text = "Internal Num") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Password") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (loginUser(context, internalNumber, cbNumber, password)) {
                        onLoginSuccess(true)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Log In")
            }
        }
    }
}

private fun loginUser(context: Context, internalNumber: String, cbNumber: String, password: String): Boolean {
    val auth = FirebaseAuth.getInstance()
    return try {
        val email = "$internalNumber@$cbNumber.com"
        val result = auth.signInWithEmailAndPassword(email, password)
        Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
        true
    } catch (e: Exception) {
        Toast.makeText(context, "Authentication failed: ${e.message}", Toast.LENGTH_SHORT).show()
        false
    }
}

@Composable
fun HomeScreen() {
    Text(text = "Home Screen")
}

@Preview
@Composable
fun LoginScreenPreview() {
    AppContent()
}