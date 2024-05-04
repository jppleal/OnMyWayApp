package com.jppleal.onmywayapp


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.jppleal.onmywayapp.data.getSomeGoodHardcodedAlerts
import com.jppleal.onmywayapp.data.model.Alert
import com.jppleal.onmywayapp.data.users
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(navController: NavController) {
    val loggedIn = false
    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
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
                }, modifier = Modifier.fillMaxWidth()
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
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            Arrangement.Center,
            Alignment.CenterHorizontally
        ) {
            TextField(value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focusRequester.requestFocus() }),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(
                modifier = Modifier.padding(8.dp)
            )
            TextField(value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                keyboardActions = KeyboardActions(onDone = {
                    if (loginUser(
                            email = email, password = password, context = context
                        ) != null
                    ) {
                        failed = false
                        navController.navigate(Screen.HomeScreen.route)

                    } else {
                        failed = true
                    }
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
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
    val context = LocalContext.current
    val userEmail =
        context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).getString("userEmail", null)
    var selectedAlertId by remember { mutableStateOf<Int?>(null) }
    var alerts by remember {
        mutableStateOf<List<Alert>?>(null)
    }
    val user = users.find { it.email.equals(userEmail, ignoreCase = true) }
    LaunchedEffect(key1 = Unit) {
        delay(3000)
        alerts = getSomeGoodHardcodedAlerts()
    }
    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        context, Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    alerts?.let { alerts ->
                        for (alert in alerts) {
                            AlertItem(alert) {}
                            NotificationUtils.showNotification(
                                context, "Nova Ocorrência!", alert.message, alert.id
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                } else {

                    // Optionally, remind the user that the notification permission is needed
                    // This could redirect them to the settings or show an explanatory dialog

                }
            } else {
                // For versions below Android 13, just show the notification
                alerts?.let { alerts ->
                    for (alert in alerts) {
                        AlertItem(alert) {}
                        NotificationUtils.showNotification(
                            context, "Nova Ocorrência!", alert.message, alert.id
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

            }/*for (alert in getSomeGoodHardCodedAlertsDelayed()) {
                AlertItem(alert = alert) {

                }
            }*/
        }
    }
}


@Composable
fun OptionScreen(navController: NavController) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Settings", style = MaterialTheme.typography.bodyLarge
            )
            HorizontalDivider(color = Color.LightGray)

            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {                         // Navigate to the app settings
                    val intent = Intent().apply {
                        action = android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS
                        putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                    context.startActivity(intent)
                }.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Option Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "Notifications", style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = "Manage notification settings",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Option Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "Dark Mode", style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = "Enable dark mode", style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Switch(checked = true, onCheckedChange = {  /*TODO: when changed fun*/ })
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Sign Out", style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = "Sign out of your account",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        navController.navigate(Screen.LogInScreen.route)
                        logOut(context)
                    }, modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Option Icon"
                    )
                }
            }
        }
    }
}
