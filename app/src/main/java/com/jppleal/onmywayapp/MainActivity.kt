package com.jppleal.onmywayapp

import android.content.Context
import android.os.Bundle
import android.widget.Space
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        val sharePref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userEmail = sharePref.getString("userEmail", "")
        if (!userEmail.isNullOrEmpty()) {
            setContent {
                AppContent()
                HomeScreen("José Leal", "935", ::logOut, getSomeGoodHardcodedAlerts())
            }
        } else {
            setContent {
                LoginScreen { success ->
                    if (success) {
                        //     HomeScreen("José Leal", "935", :: logOut, getSomeGoodHardcodedAlerts() )
                        recreate()
                    }
                }
            }
        }
    }
}

fun logOut(context: Context) {
    val sharePref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val editor = sharePref.edit()
    editor.remove("userEmail")
    editor.apply()

    (context as ComponentActivity).recreate()
}

@Composable
fun AppContent() {
    var loggedIn by remember { mutableStateOf(true) }

    if (loggedIn) {
        HomeScreen(
            userName = "José Leal",
            internalNumber = "935",
            ::logOut,
            getSomeGoodHardcodedAlerts()
        )
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
fun HomeScreen(userName: String, internalNumber: String, logoutUser: (Context) -> Unit, alerts: List<Alert>) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.padding(0.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopNavigationBar(userName = userName, internalNumber = internalNumber, logoutUser = logoutUser)
            Spacer(modifier = Modifier.height(16.dp))
            AlertList(alerts = alerts)
        }
    }
}

@Composable
fun AlertList(alerts: List<Alert>) {
    Column {
        alerts.forEach { alert ->
            AlertItem(alert = alert)
            Spacer(modifier = Modifier.height(20.dp))
            //Divider() //This adds dividers between alerts
        }
    }
}

@Composable
fun AlertItem(alert: Alert) {
    var showDialog by remember {
        mutableStateOf(false)
    }
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        ),
        modifier = Modifier.padding(10.dp, 5.dp, 10.dp, 5.dp),

        ) {
        //Alert title
        Text(
            text = alert.message,
            modifier = Modifier.padding(5.dp, 5.dp, 5.dp, 5.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(1.dp))
        // date and time of the alert
        Text(
            text = alert.dateTime.toString(),
            modifier = Modifier.padding(5.dp)
        )

        Spacer(modifier = Modifier.height(1.dp))
        // requested info from the alert
        Text(
            buildString {
                append("Solicita-se: ")
                alert.firefigthers?.let { append("$it - Bombeiros; ") }
                alert.graduateds?.let { append("$it - Graduados; ") }
                alert.truckDriver?.let { append("$it - Motoristas de pesados; ") }
            },
            modifier = Modifier.padding(5.dp)
        )
        //divider between text and buttons
        Divider(
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) { //Column or Row?
            Button(
                onClick = { showDialog = true},
                modifier = Modifier
                    .widthIn()
                    .padding(5.dp),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(Color.Green)
            ) {
                Text(
                    text = "A CAMINHO",
                    color = Color.Black
                )
            }
            if (showDialog) {
                EstimatedTimeOfArrival(onDismiss = { showDialog = false })
            }

            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .widthIn()
                    .padding(5.dp),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(Color.Red)
            ) {
                Text(
                    text = "INDISPONÍVEL",
                    color = Color.Black
                )
            }
        }
    }
}

@Composable //Lets user to let emissor know how long it will take to arrive
fun EstimatedTimeOfArrival(onDismiss: () -> Unit) {
    var selectedNumber by remember {
        mutableStateOf(0)
    }
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .width(300.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Tempo previsto de chegada: XX minutos")/*TODO*/ //indicar o tempo estimado com base na localização ou morada pré definida
                Spacer(modifier = Modifier.height(2.dp))
                Row(modifier = Modifier.padding(0.dp,1.dp,0.dp,1.dp)) {
                    //there's no segmented buttons on compose
                    TextButton(onClick = { selectedNumber = 5 },
                    modifier = Modifier
                        .padding(horizontal = 1.dp, vertical = 1.dp)
                        .background(
                            if (selectedNumber == 5) Color.LightGray else Color.Transparent
                        )){
                        Text("5")
                    }
                    Divider(color = Color.LightGray,
                        modifier = Modifier
                            .height(40.dp)
                            .width(1.dp),
                    )
                    TextButton(onClick = { selectedNumber = 10 },
                        modifier = Modifier
                            .padding(horizontal = 1.dp, vertical = 1.dp)
                            .background(
                                if (selectedNumber == 10) Color.LightGray else Color.Transparent
                            )){
                        Text("10")
                    }
                    Divider(color = Color.LightGray,
                        modifier = Modifier
                            .height(40.dp)
                            .width(1.dp),
                    )
                    TextButton(onClick = { selectedNumber = 15 },
                        modifier = Modifier
                            .padding(horizontal = 1.dp, vertical = 1.dp)
                            .background(
                                if (selectedNumber == 15) Color.LightGray else Color.Transparent
                            )){
                        Text("15")
                    }
                    Divider(color = Color.LightGray,
                        modifier = Modifier
                            .height(40.dp)
                            .width(1.dp),
                         )
                    TextButton(onClick = { selectedNumber = 16 },
                        modifier = Modifier
                            .padding(horizontal = 1.dp, vertical = 1.dp)
                            .background(
                                if (selectedNumber == 16) Color.LightGray else Color.Transparent
                            )){
                        Text("+15")
                    }
                }
                Button(onClick = onDismiss) {
                    Text("Enviar")
                }
            }
        }
    }
}

fun logOut(context: Context){
    val sharePref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val editor = sharePref.edit()
    editor.remove("userEmail")
    editor.apply()

    (context as ComponentActivity).recreate()
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationBar(userName: String, internalNumber: String, logoutUser: (Context) -> Unit) {
    val scrollBehaviour = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var showLogoutDialog by remember { mutableStateOf(false)}
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehaviour.nestedScrollConnection)
            .height(40.dp),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        text = "$userName - $internalNumber",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) { //what is this bro
                        //Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        //contentDescription = "Localized description")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showLogoutDialog = true
                    }) { //again... what is this bro
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Logout"
                        )
                    }
                },
                scrollBehavior = scrollBehaviour
            )
        },
    ) { innerPadding ->
        ScrollContent(innerPadding)

        if (showLogoutDialog){
            AlertDialog(
            onDismissRequest = {showLogoutDialog = false},
                title = {
                    Text(text = "Confirm Logout")},
                text ={ Text(text = "Are you sure you want to log out?")},
                confirmButton = {
                    Button(onClick =  {
                        logoutUser(LocalContext.current)
                    showLogoutDialog = false
                    }) {
                    Text(text = "Logout")
                    }
                },
                dismissButton = {
                    Button(onClick = {showLogoutDialog = false}) {
                        Text(text = "Cancel")
                    }
                }
            )
        }
    }
}
@Composable
fun ScrollContent(innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
    ) {
    }
}

@Composable
fun OptionScreen() {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Log out")
            }
        }
    }
}

private fun loginUser( context: Context, internalNumber: String, cbNumber: String, password: String): Boolean {
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
fun HomeScreenPreview() {
    HomeScreen("José Leal", "935", ::logOut, getSomeGoodHardcodedAlerts())
}

@Preview
@Composable
fun AlertListPreview() {
    val alerts = getSomeGoodHardcodedAlerts()
    AlertList(alerts = alerts)
}

@Preview
@Composable
fun OptionScreenPreview() {
    OptionScreen()
}

@Preview
@Composable
fun EstimatedTimeOfArrivalPreview() {
    EstimatedTimeOfArrival(onDismiss = { true })
}

//defined Alert data class
data class Alert(
    val id: Int,
    val message: String,
    val dateTime: String?,
    val firefigthers: Int?,
    val graduateds: Int?,
    val truckDriver: Int?
)

//Function for test purposes
fun getSomeGoodHardcodedAlerts(): List<Alert> {
    return listOf(
        Alert(
            1, "Inc. Urbano - Saída de VUCI 01",
            "22-02-2024 00:08",
            4, 1, 1
        ),
        Alert(
            id = 2, message = "INC. URBANO - SAÍDA DE VUCI 06",
            dateTime = "22-02-2024 00:08",
            4, 1, 1
        ),
        Alert(
            id = 3, message = "INC. URBANO - SAÍDA DE VTTU 02",
            dateTime = "22-02-2024 00:08",
            2, null, 1
        )
    )
}