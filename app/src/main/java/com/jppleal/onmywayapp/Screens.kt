package com.jppleal.onmywayapp


import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jppleal.onmywayapp.data.getSomeGoodHardcodedAlerts
import com.jppleal.onmywayapp.data.users

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
                    } else {
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
fun CredentialsForm(navController: NavController) {
    var email: String by remember { mutableStateOf("") }
    var password: String by remember { mutableStateOf("") }
    var failed: Boolean by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            Arrangement.Center,
            Alignment.CenterHorizontally
        ) {
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focusRequester.requestFocus() }),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(
                modifier = Modifier
                    .padding(8.dp)
            )
            TextField(value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                keyboardActions = KeyboardActions(onDone =
                {
                    if (loginUser(
                            email = email,
                            password = password,
                            context = context
                        ) != null
                    ) {
                        failed = false
                        navController.navigate(Screen.HomeScreen.route)

                    } else {
                        failed = true
                    }
                }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester))
            Spacer(Modifier.padding(8.dp))
            if (failed) {
                Text("Please Try Again.")
            }
            Spacer(Modifier.padding(8.dp))
            Button(onClick = {
                /*TODO: insert authentication here*/
                if (loginUser(email = email, password = password, context = context) != null) {
                    failed = false
                    navController.navigate(Screen.HomeScreen.route)

                } else {
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
    navController: NavController
) {
    var context = LocalContext.current
    val userEmail = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        .getString("userEmail", null)
    var selectedAlertId by remember { mutableStateOf<Int?>(null)}
    fun onAlertItemSelected(alertId: Int?){
        selectedAlertId = alertId
    }


    val user = users.find { it.email.equals(userEmail, ignoreCase = true) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.padding(0.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (user != null) {
                TopNavigationBar(
                    userName = user.username,
                    internalNumber = user.internalNumber.toString(),
                    navController = navController,
                    context = context
                )
            } else {
                TopNavigationBar(
                    userName = "",
                    internalNumber = "",
                    navController = navController,
                    context = context
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            //AlertList(alerts = alerts)
            for (alert in getSomeGoodHardcodedAlerts()) {
                AlertItem(alert = alert) {

                }
            }
        }
    }
}


@Composable
fun OptionScreen(navController: NavController) {
    // Option screen UI
}