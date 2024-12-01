package com.jppleal.onmywayapp

import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import com.google.firebase.database.FirebaseDatabase
import com.jppleal.onmywayapp.data.model.Alert


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationBar(
    navController: NavController,
    context: Context
) {
    val scrollBehaviour = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehaviour.nestedScrollConnection)
            .height(40.dp),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.LightGray,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        text = "On My Way App",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Screen.OptionScreen.route)
                       // logOut(context)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
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
fun AlertItem(alertData: Alert, onItemSelected: (Int) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var accepted by remember { mutableStateOf(false) }
    var declined by remember { mutableStateOf(false) }
    var estimatedTime by remember { mutableStateOf(0) }
    val formattedDate = remember(alertData.dateTime) {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault())
        sdf.format(java.util.Date(alertData.dateTime))
    }
    OutlinedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        ),
        border = BorderStroke(1.dp, color = if (accepted) Color.Green else Color.LightGray),
        modifier = Modifier.padding(10.dp, 5.dp, 10.dp, 5.dp)
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
            },
            modifier = Modifier.padding(5.dp)
        )

        // Divider entre Texto e Botões
        HorizontalDivider(modifier = Modifier.padding(5.dp), color = Color.Gray)

        // Botões de Resposta
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            if (accepted || declined) {
                // Mensagem se o alerta foi aceito ou recusado
                Text(
                    text = "Resposta: ${if (accepted) "A CAMINHO (Tempo: $estimatedTime min)" else "INDISPONÍVEL"}",
                    color = Color.Black,
                    modifier = Modifier.padding(5.dp)
                )
            } else {
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
                        updatedAlertResponse(alertData, "Indisponível")
                    },
                    modifier = Modifier.padding(5.dp),
                    colors = ButtonDefaults.buttonColors(Color.Red)
                ) {
                    Text(text = "INDISPONÍVEL", color = Color.White)
                }
            }
        }
    }

    if (showDialog) {
        EstimatedTimeDialog(
            onDismiss = { showDialog = false },
            onTimeSelected = { time ->
                accepted = true
                estimatedTime = time
                updatedAlertResponse(alertData, "A Caminho", time)
                showDialog = false
            }
        )
    }
}

@Composable
fun EstimatedTimeDialog(onDismiss: () -> Unit, onTimeSelected: (Int) -> Unit) {
    var selectedTime by remember { mutableStateOf(5) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Selecione o Tempo Estimado de Chegada")
                Spacer(modifier = Modifier.height(16.dp))
                // Segmento de botões para selecionar o tempo
                Row {
                    Button(onClick = { selectedTime = 5 }) { Text("5 min") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { selectedTime = 10 }) { Text("10 min") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { selectedTime = 15 }) { Text("15 min") }
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
    ) {
    }
}

