package com.jppleal.onmywayapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.jppleal.onmywayapp.data.model.Alert
import com.jppleal.onmywayapp.data.model.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationBar(
    navController: NavController
) {
    val scrollBehaviour = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehaviour.nestedScrollConnection)
            .height(40.dp),
        topBar = {
            CenterAlignedTopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.LightGray,
                titleContentColor = MaterialTheme.colorScheme.primary
            ), title = {
                Text(
                    text = "On My Way App", maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }, actions = {
                IconButton(onClick = {
                    navController.navigate(Screen.OptionScreen.route)
                    // logOut(context)
                }) {
                    Icon(
                        imageVector = Icons.Filled.Settings, contentDescription = "Settings"
                    )
                }
            }, scrollBehavior = scrollBehaviour
            )
        },
    ) { innerPadding ->
        ScrollContent(innerPadding)
    }
}

@Composable
fun AlertItem(alertData: Alert, onItemSelected: (Int) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var accepted by remember { mutableStateOf(false) }
    var declined by remember { mutableStateOf(false) }
    var estimatedTime by remember { mutableStateOf(0) }
    val formattedDate = remember(alertData.dateTime) {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        sdf.format(Date(alertData.dateTime))
    }

    val userId = SharedPrefsManager.getUserId()
    val isResponded = alertData.responded?.containsKey(userId) == true
    val status = alertData.responded?.get(userId)?.status

    LaunchedEffect(alertData) {
        fetchUserResponse(alertData){response ->
            response?.let {
                estimatedTime = it.estimatedTime ?: 0
            }
        }
    }

    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (accepted) Color(0xFFB0BEC5) else Color(
                0xFFFFFFFF
            )
        ),
        border = BorderStroke(1.dp, color = if (accepted) Color.Green else Color.LightGray),
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp) //.clickable{ }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Título do Alerta
            Text(
                text = alertData.message,
                modifier = Modifier.padding(5.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(1.dp))
            // Data e Hora do Alerta
            Text(
                text = "Data/Hora: $formattedDate",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(5.dp),
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(1.dp))
            // Informação Solicitada
            Text(
                buildString {
                    append("Solicita-se: \n")
                    alertData.firefighters?.let { append("$it - Bombeiros; ") }
                    alertData.graduated?.let { append("$it - Graduados; ") }
                    alertData.truckDriver?.let { append("$it - Motoristas de pesados; ") }
                }, modifier = Modifier.padding(5.dp)
            )

            if (isResponded) {
                //it was answered already
                Text(
                    text = if (status == true) "Resposta: A CAMINHO (Tempo: $estimatedTime min)" else "Resposta: INDISPONÍVEL",
                    color = if (status == true) Color.Green else Color.Red,
                    modifier = Modifier.padding(5.dp)
                )
            } else {

                // Divider entre Texto e Botões
                HorizontalDivider(modifier = Modifier.padding(5.dp), color = Color.Gray)

                // Botões de Resposta
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ) {
                    // Botão "A Caminho"
                    Button(
                        onClick = {
                            showDialog = true
                            onItemSelected(alertData.id)
                        },
                        modifier = Modifier.padding(5.dp),
                        colors = ButtonDefaults.buttonColors(Color.Green)
                    ) {
                        Text(text = "A CAMINHO", color = Color.White)
                    }
                    // Botão "Indisponível"
                    Button(
                        onClick = {
                            declined = true
                            updatedAlertResponse(alertData, false)
                        },
                        modifier = Modifier.padding(5.dp),
                        colors = ButtonDefaults.buttonColors(Color.Red)
                    ) {
                        Text(text = "INDISPONÍVEL", color = Color.White)
                    }
                }
            }
        }
    }

    if (showDialog) {
        EstimatedTimeDialog(onDismiss = { showDialog = false }, onTimeSelected = { time ->
            accepted = true
            estimatedTime = time
            updatedAlertResponse(alertData, true, time)
            showDialog = false
        })
    }
}

@Composable
fun EstimatedTimeDialog(onDismiss: () -> Unit, onTimeSelected: (Int) -> Unit) {
    var selectedTime by remember { mutableStateOf(5) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.padding(8.dp, 32.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                    Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Selecione o tempo estimado de chegada")
                Spacer(modifier = Modifier.height(16.dp))
                //Segmento de botões para selecionar o tempo
                Row {
                    Button(onClick = { selectedTime = 5 }) { Text(text="5 min", maxLines = 1, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)}
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { selectedTime = 10 }) { Text(text="10 min", maxLines = 1, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)}
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { selectedTime = 15 }) { Text(text="15 min", maxLines = 1, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)}
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { onTimeSelected(selectedTime) }) {
                    Text("Confirmar")
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
    ) {}
}

