package com.jppleal.onmywayapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseApp
import java.sql.Timestamp
import kotlin.reflect.KFunction1

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        val sharePref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userEmail = sharePref.getString("userEmail","")
        if (!userEmail.isNullOrEmpty()){
            setContent{
                HomeScreen("José Leal", "935", :: logOut, getSomeGoodHardcodedAlerts())
            }
        }else{
            setContent {
                LoginScreen{
                    success -> if (success){
                   //     HomeScreen("José Leal", "935", :: logOut, getSomeGoodHardcodedAlerts() )
                    }
                }
                AppContent()
            }
        }
    }
}

fun logOut(context: Context){
    val sharePref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val editor = sharePref.edit()
    editor.remove("userEmail")
    editor.apply()
}
@Composable
fun AppContent(){
    var loggedIn by remember { mutableStateOf(false)}

    if (!loggedIn){
        LoginScreen(onLoginSuccess = {
            success -> loggedIn = success
        })
        }else{
            HomeScreen(userName = "José Leal", internalNumber = "935", ::logOut, getSomeGoodHardcodedAlerts())
    }
}
@Composable
fun LoginScreen(onLoginSuccess: (Boolean) -> Unit) {
    var cbNumber by remember { mutableStateOf("") }
    var internalNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var context = LocalContext.current
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
fun HomeScreen(userName: String, internalNumber: String, onLogout: (Context) -> Unit, alerts: List<Alert>) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){
        Column (
            modifier = Modifier.padding(0.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
            ){
            TopNavigationBar(userName = userName, internalNumber = internalNumber ) {
             //goes to profile?
            }
            Spacer(modifier = Modifier.fillMaxHeight())
            Text(text = "It's quiet here... No news... Good news?")
            AlertList(alerts = alerts)
        }
    }
}
@Composable
fun AlertList(alerts: List<Alert>){
    Column {
        alerts.forEach{
                alert ->
            AlertItem(alert = alert)
            Divider() //This adds dividers between alerts
        }
    }
}
@Composable
fun AlertItem(alert: Alert){
    Text(text = alert.message)
}
@Composable
fun TopNavigationBar(userName: String, internalNumber: String, onUserNameClicked: () -> Unit){
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth()
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,

        ){
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
    }
}
@Composable
fun OptionScreen(onLogout: ()->Unit){
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ){
        Column (
            modifier = Modifier.padding(0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = { onLogout() },
                modifier = Modifier.padding(8.dp)) {
                Text(text = "Log out")
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
        editor.putString("internalNum", internalNumber)
        editor.putString("numCB", cbNumber)
        editor.apply()
        true
    } catch (e: Exception) {
        Toast.makeText(context, "Authentication failed: ${e.message}", Toast.LENGTH_SHORT).show()
        false
    }
}
@Preview
@Composable
fun LoginScreenPreview() {
    AppContent()
}
@Preview
@Composable
fun HomeScreenPreview(){
    HomeScreen("José Leal", "935", ::logOut, getSomeGoodHardcodedAlerts())
}
@Preview
@Composable
fun AlertListPreview(){
    val alerts = getSomeGoodHardcodedAlerts()
    AlertList(alerts = alerts)
}

@Preview
@Composable
fun OptionScreenPreview(){
    OptionScreen(onLogout = {})
}

//defined Alert data class
data class Alert(
    val id: Int,
    val message: String,
    val timestamp: Long
)
//Function for test purposes
fun getSomeGoodHardcodedAlerts(): List<Alert>{
    return listOf(
        Alert(
            id=1, message = "Inc. Urbano - Saída de VUCI 01",
            timestamp = System.currentTimeMillis() //this will have the timestamp from the server
        ),
        Alert(
            id=2, message = "INC. URBANO - SAÍDA DE VUCI 06",
            timestamp = System.currentTimeMillis()
        ),
        Alert(
            id = 3, message = "INC. URBANO - SAÍDA DE VTTU 02",
            timestamp = System.currentTimeMillis()
        )
    )
}