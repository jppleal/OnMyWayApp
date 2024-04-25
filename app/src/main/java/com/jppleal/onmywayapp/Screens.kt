package com.jppleal.onmywayapp


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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jppleal.onmywayapp.data.model.Alert

@Composable
fun LoginScreen(navController: NavController) {
    val loggedIn = false
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
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    if (loggedIn) {
                        navController.navigate(Screen.HomeScreen.route)
                    }else{
                        navController.navigate(Screen.CredentialsForm.route)
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
fun CredentialsForm(navController: NavController){
    var email: String by remember{ mutableStateOf("")}
    var password: String by remember { mutableStateOf("")}
    var failed : Boolean by remember { mutableStateOf(false)}
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            Arrangement.Center,
            Alignment.CenterHorizontally
        ) {
            TextField(value = email,
                onValueChange = {email = it},
                label = {Text("Email")},
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
                )
            Spacer(modifier = Modifier
                .padding(8.dp)
            )
            TextField(value = password,
                onValueChange = {email = it},
                label = {Text("Password")},
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.padding(8.dp))
            if(failed){
                Text("Please Try Again.")
            }
            Spacer(Modifier.padding(8.dp))
            Button(onClick = {
                /*TODO: insert authentication here*/
                if (loginUser(email, password) != null) {
                    navController.navigate(Screen.HomeScreen.route)
                    failed = false
                }else{
                    failed = true
                }
            }) {
                Text("Log In")
            }
            Spacer(modifier = Modifier.padding(8.dp))
            Button(onClick = { navController.navigate(Screen.LogInScreen.route) }) {
                Text("Back")
            }
        }

    }
}


@Composable
fun HomeScreen(
    userName: String,
    internalNumber: String,
    alerts: List<Alert>,
    navController: NavController
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.padding(0.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopNavigationBar(userName = userName, internalNumber = internalNumber)
            Spacer(modifier = Modifier.height(16.dp))
            AlertList(alerts = alerts)
        }
    }
}


@Composable
fun OptionScreen(navController: NavController) {
    // Option screen UI
}