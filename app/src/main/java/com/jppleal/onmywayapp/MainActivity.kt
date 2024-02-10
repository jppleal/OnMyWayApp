package com.jppleal.onmywayapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Space
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        val sharePref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userEmail = sharePref.getString("userEmail","")
        if (!userEmail.isNullOrEmpty()){
            setContent{
                HomeScreen(userName = "José Leal", internalNumber = "935")
            }
        }else{
            setContent {
                LoginScreen{
                    success -> if (success){
                    }
                }
                AppContent()
            }
        }
    }
}
@Composable
fun AppContent(){
    var loggedIn by remember { mutableStateOf(false)}
    val context = LocalContext.current
    LoginScreen{ success ->
        if (success) {
            loggedIn = true
        }
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


@Composable
fun HomeScreen( userName: String, internalNumber: String) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){
        Column (
            modifier = Modifier.padding(0.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
            ){
            TopNavigationBar(userName = userName, internalNumber =internalNumber ) {
             //goes to profile?
            }
            Spacer(modifier = Modifier.fillMaxHeight())
            Text(text = "It's quiet here... No news... Good news?")
        }
    }
}
@Composable
fun TopNavigationBar(userName: String, internalNumber: String, onUserNameClicked: () -> Unit){
    val context = LocalContext.current
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxWidth()
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ){
            Column {
                ClickableText(
                    text = AnnotatedString(userName),
                    onClick = { offset ->
                        onUserNameClicked()
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(10.dp,0.dp,0.dp,0.dp)
                )
                Text(
                    text = AnnotatedString("($internalNumber)"),
                    style = MaterialTheme.typography.bodyMedium
                )

            }

            IconButton(
                onClick = {onOptionsButtonClicked()},
                modifier = Modifier.size(48.dp)
            ){
                Icon(Icons.Default.Settings, contentDescription = "Options" )
            }
        }
    }
}
private fun loginUser(context: Context, internalNumber: String, cbNumber: String, password: String): Boolean {
    val auth = FirebaseAuth.getInstance()
    val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    return try {
        val email = "$internalNumber@$cbNumber.com"
        val result = auth.signInWithEmailAndPassword(email, password)
        Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
        editor.putString("userEmail", email)
        editor.apply()
        true
    } catch (e: Exception) {
        Toast.makeText(context, "Authentication failed: ${e.message}", Toast.LENGTH_SHORT).show()
        false
    }
}

@Composable
fun onOptionsButtonClicked(){
    val context = LocalContext.current
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth()
    ){
        Column {
            Text(text = "some text in the options")
        }
    }
}
fun LogOut( sharedPref: SharedPreferences){
    val editor = sharedPref.edit()
    editor.remove("userEmail")
    editor.apply()
}
@Preview
@Composable
fun LoginScreenPreview() {
    AppContent()
}

@Preview
@Composable
fun HomeScreenPreview(){
    HomeScreen("José Leal", "935")
}

@Preview
@Composable
fun onOptionsButtonClickedPreview(){
    onOptionsButtonClicked()
}