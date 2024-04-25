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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jppleal.onmywayapp.data.model.Alert

@Composable
fun LoginScreen(navController: NavController) {
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
                    navController.navigate(Screen.HomeScreen.route)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Log In")
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