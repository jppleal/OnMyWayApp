package com.jppleal.onmywayapp

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.jppleal.onmywayapp.data.model.Alert


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationBar(
    userName: String,
    internalNumber: String,
    navController : NavController,
    context: Context
) {
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
                    IconButton(onClick = {
                        navController.navigate(Screen.LogInScreen.route)
                        logOut(context)
                    }) {
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
fun AlertList(alerts: List<Alert>) {
    Column {
        alerts.forEach { alert ->
            AlertItem(alert = alert)
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
