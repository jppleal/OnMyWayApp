package com.jppleal.onmywayapp

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.util.Log
import com.jppleal.onmywayapp.NotificationUtils

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let {
            val title = it.title ?: "Alerta!"
            val message = it.body ?: "Recebeste um novo alerta."
            showNotification(title, message)
        }
    }
    private fun showNotification(title: String, message: String) {
        // Utiliza a tua NotificationUtils para mostrar a notificação
        NotificationUtils.showNotification(applicationContext, title, message, 0)
    }
}
