package com.jppleal.onmywayapp.data

import android.content.Context
import android.media.MediaPlayer
import com.jppleal.onmywayapp.R
import com.jppleal.onmywayapp.data.model.Alert
import com.jppleal.onmywayapp.data.model.CB
import com.jppleal.onmywayapp.data.model.User
import com.jppleal.onmywayapp.data.model.UserFunction
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun getSomeGoodHardCodedAlertsDelayed(): List<Alert> = runBlocking {
    delay(2000)
    getSomeGoodHardcodedAlerts()
}
fun getSomeGoodHardcodedAlerts(): List<Alert>{
    return listOf(
        Alert(
            1, "Inc. Urbano - Saída de VUCI 01",
            "22-02-2024 00:08",
            4, 1, 1
        )/*,
        Alert(
            id = 2, message = "INC. URBANO - SAÍDA DE VUCI 06",
            dateTime = "22-02-2024 00:08",
            4, 1, 1
        ),
        Alert(
            id = 3, message = "INC. URBANO - SAÍDA DE VTTU 02",
            dateTime = "22-02-2024 00:08",
            2, null, 1
        )*/
    )
}
fun playNotificationSound(context: Context){
    val mediaPlayer = MediaPlayer.create(context, R.raw.alertSound)
    mediaPlayer.start()
}
val users = listOf(
    User(
        935,
        "José Leal",
        "jose.leal@bvamm.pt",
        false,
        CB(1144),
        listOf(UserFunction.LIGHTDRIVER, UserFunction.OPTEL)
    ),
    User(
        933,
        "Jéssica Lino",
        "jessica.lino@bvamm.pt",
        false,
        CB(1144),
        listOf(UserFunction.TAS, UserFunction.OPTEL)
    ),
    User(
        999,
        "Nuno Cruz",
        "nuno.cruz@bvamm.pt",
        false,
        CB(1144),
        listOf(UserFunction.TRUCKDRIVER, UserFunction.GRADUATED)
    ),
    User(0,
        "Central",
        "central.user@bvamm.pt",
        true,
        CB(1144),
        listOf(UserFunction.OPTEL)
        ),
    User(123,
        "Marco Cabo",
        "marco.cabo@bvamm.pt",
        true,
        CB(1144),
        listOf(UserFunction.OPTEL,UserFunction.TRUCKDRIVER,UserFunction.TAS,UserFunction.OPTEL,UserFunction.GRADUATED))
)

val userPasswords = mapOf("jose.leal@bvamm.pt" to "cb1144",
    "nuno.cruz@bvamm.pt" to "cb1144")
