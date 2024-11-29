package com.jppleal.onmywayapp

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.util.Log

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
        Log.d("FCM", "Mensagem recebida: ${remoteMessage.data}")
    }
    private fun showNotification(title: String, message: String) {
        // Utiliza a tua NotificationUtils para mostrar a notificação
        NotificationUtils.showNotification(applicationContext, title, message, 0)
    }
}
