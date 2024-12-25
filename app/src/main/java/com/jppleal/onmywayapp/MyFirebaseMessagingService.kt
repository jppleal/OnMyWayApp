package com.jppleal.onmywayapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.util.Log
import androidx.core.app.NotificationCompat

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        //Verifica se é uma notificação
        remoteMessage.notification?.let {
            sendNotification(it.title, it.body)
        }

        //processa dados adicionais se existirem
        remoteMessage.data.let{ data ->
            if(data.isNotEmpty()){
                val extraInfo = data["extraInfo"]
                println("Dados adicionais recebidos: $extraInfo")
            }
        }
    }

    private fun sendNotification(title: String?, body:String?){
        val channelId = "alerts_channel"
        val notificationId = System.currentTimeMillis().toInt()

        //Intenção ao clicar na notificação
        val intent = Intent(this, MainActivity::class.java) //Trocar para a activity correta
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //Criação do canal de notificações
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        //Criar notificação
        val notification = NotificationCompat.Builder(this,channelId)
            .setContentTitle(title ?: "Novo Alerta")
            .setContentText(body ?: "Solicita-se resposta!")
            .setSmallIcon(R.drawable.jose_logo_01)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("TOKEN", "$token")
    }
}
