package com.jppleal.onmywayapp


import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseApp
import com.jppleal.onmywayapp.R.layout.activity_login
import com.jppleal.onmywayapp.ui.theme.OnMyWayAppTheme

class MainActivity : ComponentActivity() {
    private val auth = FirebaseAuth.getInstance();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            OnMyWayAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Greeting("Android")
                    LoginScreen()
                }
            }
        }
    }

    @Composable
    fun LoginScreen() {
        val cbNumber = findViewById<EditText>(R.id.numCB).text.toString()
        val internalNumber = findViewById<EditText>(R.id.numInternal).text.toString()
        val password = findViewById<EditText>(R.id.password).text.toString() //this must be hashed??

        val onLoginClick: () -> Unit = {
            authenticateUser(cbNumber, internalNumber, password)
        }

        AndroidView(modifier = Modifier.fillMaxSize(),
            factory = { context ->
                LayoutInflater.from(context).inflate(activity_login, null, false)
            },
                update = { view ->
                    val loginButton = view.findViewById<Button>(R.id.login)
                    loginButton.setOnClickListener{
                        onLoginClick()
                    }
                }
        )
    }

    //this method is missing the cbnumber to check the log in
    private fun authenticateUser(cbNumber: String, internalNumber: String, password: String) {
        auth.signInWithEmailAndPassword(internalNumber, password).addOnCompleteListener{
            task ->
            if (task.isSuccessful){ //maybe had a condition where the cbNumber has to match
                //navigation logic here
            }else{
                //auth failed
            }
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