package com.jppleal.onmywayapp

import android.app.Activity
import android.content.ContentProviderClient
import android.content.Context
import android.os.Bundle
import android.os.CancellationSignal.OnCancelListener
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.runtime.saveable.autoSaver
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
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private val  RC_SIGN_IN = 1001
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //FirebaseApp.initializeApp(this)
        val sharePref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userEmail = sharePref.getString("userEmail", "")
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_ID))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent{
            val navController = rememberNavController()
            AppContent(navController = navController)
        }
    }
}
@Composable
fun NavHost(
    navController: NavController,
    startDestination: String,
    modifier: Modifier,
    builder: NavGraphBuilder.() -> Unit
): Unit {

}

enum class Route {
    LogInScreen,
    HomeScreen,
    OptionsScreen

}

@Composable
fun AppContent(navController: NavController) {
    Scaffold { padding ->
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn()) Route.HomeScreen.name else Route.LogInScreen.name,
            modifier = Modifier.padding(padding)
        )
        {
            composable(route = Route.LogInScreen.name) {
                LoginScreen(
                    googleSignInClient = googleSignInClient,
                    requestCode = RC_SIGN_IN,
                    auth = auth,
                    navController = navController,
                    onLoginSuccess = {
                        success ->
                        if (success){
                            savedLoggedInState(true)
                        }
                    }
                )
            }
            composable(route = Route.HomeScreen.name) {
                HomeScreen(
                    userName = "",
                    internalNumber = "",
                    logoutUser = {
                        savedLoggedInState(false)
                        navController.navigate(Route.LogInScreen.name)
                    },
                    alerts = getSomeGoodHardcodedAlerts() // I can load the alerts that are up in here
                )
            }
            composable(route = Route.OptionsScreen.name) {
                OptionScreen()
            }

        }

    }
}

private fun savedLoggedInState(loggedIn: Boolean) {

}

private fun isLoggedIn(): Boolean {
    return true
}

@Composable
fun LoginScreen(googleSignInClient: GoogleSignInClient, requestCode: Int, auth: FirebaseAuth,  navController: NavController, onLoginSuccess: (Boolean) -> Unit) {
    val context = LocalContext.current
    val signInResult = remember {
        mutableStateOf<Task<GoogleSignInAccount>?>(null)}

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
                    val signInIntent = googleSignInClient.signInIntent
                    (context as Activity).startActivityForResult(signInIntent, requestCode)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Log In with Google")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    /*TODO*/
                }, modifier = Modifier.fillMaxWidth()
            ){
                Text(text = "Log In with Microsoft (not available)")
            }
        }
    }

    val account = signInResult.value?.result
    if(account != null){
        firebaseAuthWithGoogle(account, auth, navController, onLoginSuccess)
    }else{
        signInResult.value?.addOnCompleteListener{ task ->
            if (task.isSuccessful){
                val result = task.result
                firebaseAuthWithGoogle(result, auth, navController, onLoginSuccess)
            }else{
                val exception = task.exception
            }
        }
    }
}

private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?, auth: FirebaseAuth, navController: NavController, onLoginSuccess: (Boolean) -> Unit){
    val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
    auth.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful){
                val user = auth.currentUser
                onLoginSuccess(true)
                navController.navigate(Route.HomeScreen.name)
            }else{
                Log.e("Login","Google sign in failed", task.exception)
            }
        }
}

@Composable
fun HomeScreen(
    userName: String,
    internalNumber: String,
    logoutUser: (Context) -> Unit,
    alerts: List<Alert>
) {
    val dummyFunction: () -> Unit = {}
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.padding(0.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopNavigationBar(userName = userName, internalNumber = internalNumber, onLogoutClick = dummyFunction)
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
                onClick = { showDialog = true },
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
                Row(modifier = Modifier.padding(0.dp, 1.dp, 0.dp, 1.dp)) {
                    //there's no segmented buttons on compose
                    TextButton(
                        onClick = { selectedNumber = 5 },
                        modifier = Modifier
                            .padding(horizontal = 1.dp, vertical = 1.dp)
                            .background(
                                if (selectedNumber == 5) Color.LightGray else Color.Transparent
                            )
                    ) {
                        Text("5")
                    }
                    Divider(
                        color = Color.LightGray,
                        modifier = Modifier
                            .height(40.dp)
                            .width(1.dp),
                    )
                    TextButton(
                        onClick = { selectedNumber = 10 },
                        modifier = Modifier
                            .padding(horizontal = 1.dp, vertical = 1.dp)
                            .background(
                                if (selectedNumber == 10) Color.LightGray else Color.Transparent
                            )
                    ) {
                        Text("10")
                    }
                    Divider(
                        color = Color.LightGray,
                        modifier = Modifier
                            .height(40.dp)
                            .width(1.dp),
                    )
                    TextButton(
                        onClick = { selectedNumber = 15 },
                        modifier = Modifier
                            .padding(horizontal = 1.dp, vertical = 1.dp)
                            .background(
                                if (selectedNumber == 15) Color.LightGray else Color.Transparent
                            )
                    ) {
                        Text("15")
                    }
                    Divider(
                        color = Color.LightGray,
                        modifier = Modifier
                            .height(40.dp)
                            .width(1.dp),
                    )
                    TextButton(
                        onClick = { selectedNumber = 16 },
                        modifier = Modifier
                            .padding(horizontal = 1.dp, vertical = 1.dp)
                            .background(
                                if (selectedNumber == 16) Color.LightGray else Color.Transparent
                            )
                    ) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationBar(userName: String, internalNumber: String, onLogoutClick: () -> Unit) {
    val scrollBehaviour = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val showDialog = remember { mutableStateOf(false) }
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
                actions = {
                    IconButton(onClick = { showDialog.value = true }) {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                },
                scrollBehavior = scrollBehaviour
            )
        },
    ) { innerPadding ->
        ScrollContent(innerPadding)
    }

    if (showDialog.value) {
        LogOutDialog(
            onDismiss = {
                showDialog.value = false
            },
            onConfirmation = {
                Firebase.auth.signOut()

                showDialog.value = false
                onLogoutClick()
            },
            onCancel = {
                showDialog.value = false //Dismiss dialog without logging out
            }
        )
    }
}

@Composable
fun LogOutDialog(onDismiss: () -> Unit, onConfirmation: () -> Unit, onCancel: () -> Unit) {
    val context = LocalContext.current
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("De certeza que pretende fazer logout?")
                Spacer(modifier = Modifier.height(2.dp))
                Row(modifier = Modifier.padding(0.dp, 1.dp, 0.dp, 1.dp)) {
                    //there's no segmented buttons on compose
                    TextButton(
                        onClick =
                        onConfirmation,
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        Text("Sim")
                    }
                    TextButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        Text("Não")
                    }
                }
            }
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

private fun logOut(context: Context) {
    val sharePref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val editor = sharePref.edit()
    editor.remove("userEmail")
    editor.apply()

    (context as ComponentActivity).recreate()
}


@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen()
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

@Preview
@Composable
fun LogOutDialogPreview() {
    LogOutDialog(onDismiss = { false }, onConfirmation = { true }, onCancel = { true })
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