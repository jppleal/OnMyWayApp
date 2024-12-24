package com.jppleal.onmywayapp


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Space
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.firebase.database.FirebaseDatabase
import com.jppleal.onmywayapp.data.model.Alert
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = AuthFireB()

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
                    //TODO: Verificar se o usuário está logged in
                    if (auth.isLogged()) {
                        Toast.makeText(context, "Welcome back!", Toast.LENGTH_SHORT).show()
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
    val failed: Boolean by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val auth = AuthFireB()
    var isError by remember {
        mutableStateOf(false)
    }

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
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                isError = isError && email.isBlank(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focusRequester.requestFocus() }),
                modifier = Modifier.fillMaxWidth()
            )
            if (isError && email.isBlank()){
                Text(
                    text = "Preencha o campo",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                isError = isError && password.isBlank(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                keyboardActions = KeyboardActions(onDone = {
                    auth.loginUser(email, password, onSuccess = {
                        Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                    }, onFailure = { exception ->
                        Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                        Log.e("LoginError", "Login error: ${exception.message}")
                    })

                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
            if (isError && password.isBlank()){
                Text(
                    text = "Preencher o campo",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.padding(8.dp))
            if (failed) {
                Text("Please Try Again.")
            }
            Spacer(Modifier.padding(8.dp))
            Button(onClick = {
                isError = email.isBlank() || password.isBlank()
                if(!isError){
                auth.loginUser(email, password, onSuccess = {
                    Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                    navController.navigate(Screen.HomeScreen.route)
                }, onFailure = { exception ->
                    Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                    Log.e("LoginError", "Login error: ${exception.message}")
                })
            }}, enabled = email.isNotBlank() || password.isNotBlank()) {
                Text("Log In")
            }
            Spacer(modifier = Modifier.padding(8.dp))
            Button(onClick = { navController.navigate(Screen.LogInScreen.route) }) {
                Text("Back")
            }
        }

    }
}

//HOME SCREEN NOTES
//1. There should be a function just to load new alerts and clean old ones. This function should be used in the LaunchedEffect()
//2. To avoid double notifications and to simplify the code, we should conditionally show the notification.
@Composable
fun HomeScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val alerts = remember { mutableStateListOf<Alert>() } //to storage the alerts received
    val isAnyAlertResponded = remember { mutableStateOf(false)}

    LaunchedEffect(Unit) {
        fetchNewAlertsFromBackend(alerts)
    }

    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.padding(0.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopNavigationBar(
                navController = navController
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (alerts.isEmpty()) {
                Text(text = "Nenhum alerta de momento.", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn {
                    items(alerts) { alert: Alert ->
                        AlertItem(alertData = alert, isAnyAlertResponded = isAnyAlertResponded) { alertId ->
                            //Tratamento ao selecionar um alerta

                        }
                    }
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            HandleNotifications(alerts, context)
        } else {
            alerts.forEach { alert ->
                showAlertNotification(context, alert)
            }
        }
    }
}

@Composable
fun HandleNotifications(alerts: List<Alert>, context: Context) {
    if (ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        alerts.forEach { alert ->
            showAlertNotification(context, alert)
        }
    } else {
        //TODO: Handle the permission request
    }
}

fun showAlertNotification(context: Context, alert: Alert) {
    NotificationUtils.showNotification(
        context, "Nova Ocorrência!", alert.message, alert.dateTime.toInt()
    )
}

@Composable
fun OptionScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val auth = AuthFireB()
    val notificationService = NetworkModule.notificationService
    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Settings", style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        navController.navigate(Screen.HomeScreen.route)
                    }, modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Option Icon",
                        Modifier.size(28.dp)
                    )
                }
            }
            HorizontalDivider(color = Color.LightGray)

            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {                         // Navigate to the app settings
                        val intent = Intent().apply {
                            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                            putExtra(
                                Settings.EXTRA_APP_PACKAGE, context.packageName
                            )
                        }
                        context.startActivity(intent)
                    }
                    .fillMaxWidth()) {
                Column {
                    Text(text = "Notifications", style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = "Manage notification settings",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Option Icon",
                    modifier = Modifier.size(24.dp)
                )
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
                        auth.logoutUser()
                    }, modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Option Icon",
                        Modifier.size(24.dp)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Add new user", style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = "Register new user. (only available for admins)",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    navController.navigate(Screen.NewUserFormScreen.route)

                }, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add User",
                        Modifier.size(24.dp)
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {// Send test alert
                        addAlertToFirebase(onSuccess = {
                            Toast
                                .makeText(
                                    context, "Alert sent Successfully", Toast.LENGTH_SHORT
                                )
                                .show()
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    notificationService.sendAlert(
                                        title = "Test Alert", "This is a test sent from the App!"
                                    )
                                    withContext(Dispatchers.Main) {
                                        Toast
                                            .makeText(
                                                context,
                                                "Alert sent Successfully",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                        Log.e("AlertSent", "Alert sent Successfully")

                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        Toast
                                            .makeText(
                                                context, "Alert sent failed.", Toast.LENGTH_SHORT
                                            )
                                            .show()
                                        Log.e("AlertSent", "Alert sent error: ${e.message}")
                                    }
                                }
                            }
                        }, onFailure = { exception ->
                            Toast
                                .makeText(context, "Alert sent failed.", Toast.LENGTH_SHORT)
                                .show()
                            Log.e("AlertSent", "Alert sent error: ${exception.message}")
                        })
                    }
                    .fillMaxWidth()) {
                Column {
                    Text(
                        text = "Send Alert", style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Send an alert to test", style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Test Alert Button",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun NewUserFormScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    val context = LocalContext.current
    val auth = AuthFireB()

    //Valências
    var isBombeiro by remember { mutableStateOf(false)}
    var isMotoristaPesados by remember { mutableStateOf(false) }
    var isGraduado by remember { mutableStateOf(false) }
    var isOptel by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), verticalArrangement = Arrangement.Center
    ) {
        TextField(value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = name,
            onValueChange = {name = it},
            label = {
                Text("Nome")},
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Unspecified, imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password, imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Valências", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isBombeiro,
                onCheckedChange = {isBombeiro = it}
            )
            Text("Bombeiro", modifier = Modifier.padding(start = 8.dp))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isMotoristaPesados,
                onCheckedChange = {isMotoristaPesados = it}
            )
            Text("Motorista de Pesados", modifier = Modifier.padding(start = 8.dp))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isGraduado,
                onCheckedChange = {isGraduado = it}
            )
            Text("Graduado", modifier = Modifier.padding(start = 8.dp))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isOptel,
                onCheckedChange = {isOptel = it}
            )
            Text("Operador de Central", modifier = Modifier.padding(start = 8.dp))
        }
        Button(
            onClick = {
                val skills = mapOf(
                    "bombeiro" to isBombeiro,
                    "motorista_pesados" to isMotoristaPesados,
                    "graduado" to isGraduado,
                    "optel" to isOptel
                )
                auth.registerUser(email = email.lowercase(Locale.getDefault()), password = password, name = name, onSuccess = {
                    FirebaseDatabase.getInstance().getReference("users/${auth.currentUser()}/skills")
                        .setValue(skills)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Skills saved", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Error saving skills", Toast.LENGTH_SHORT).show()
                            Log.e("RegistrationError", "Error saving skills: ${e.message}")
                        }
                    Toast.makeText(
                        context, "Registration Successful", Toast.LENGTH_SHORT
                    ).show()
                }, onFailure = { exception ->
                    Toast.makeText(context, "Registration Failed", Toast.LENGTH_SHORT).show()
                    Log.e(
                        "RegistrationError", "Registration error: ${exception.message}"
                    )
                })
            }, modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Register")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                navController.navigate(Screen.OptionScreen.route)
            }, modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Back")
        }
    }
}

