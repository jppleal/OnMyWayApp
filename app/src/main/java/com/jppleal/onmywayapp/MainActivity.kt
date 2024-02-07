package com.jppleal.onmywayapp


import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseApp
import com.jppleal.onmywayapp.ui.theme.OnMyWayAppTheme
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            LoginScreen()
        }
    }

    @Composable
    fun LoginScreen() {
        var cbNumber by remember{ mutableStateOf("")}
        var internalNumber by remember{ mutableStateOf("")}
        var password by remember { mutableStateOf("")} //this must be hashed??
        val context = LocalContext.current

        Surface ( //start of Compose being use for the login page
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ){
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Image(painter = painterResource(id = R.drawable.jose_logo_01),
                    contentDescription = "Logo",
                    modifier = Modifier.size(120.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = cbNumber ,
                    onValueChange = { cbNumber = it},
                    label ={ Text(text = "Nº CB")},
                    modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = internalNumber,
                    onValueChange = {internalNumber = it},
                    label = { Text(text = "Internal Num")},
                    modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = password,
                    onValueChange = {password = it},
                    label = { Text(text = "Password") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { loginUser(context, internalNumber, cbNumber, password) },
                    modifier = Modifier.fillMaxWidth()
                )
                {
                    Text(text = "Log In")

                }
            }
        }
    }
    fun loginUser(context: Context, internalNumber: String, cbNumber: String, password: String){
        try {
            val email = "$internalNumber@$cbNumber.com"
            val result = auth.signInWithEmailAndPassword(email, password)
            Toast.makeText(context, "Loginsuccesful", Toast.LENGTH_SHORT).show()
        }catch (e: Exception){
            Toast.makeText(context, "Authentication failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview(){
        OnMyWayAppTheme {
            Text("Nop, not good. But at least got here.")
        }
    }
}