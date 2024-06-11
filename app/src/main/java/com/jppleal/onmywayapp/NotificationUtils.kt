package com.jppleal.onmywayapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationUtils {

    private const val CHANNEL_ID = "1"

    // Call this method from your Application class or MainActivity to setup channel
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "General Notifications"
            val descriptionText = "Notifications for general purposes"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    fun showNotification(context: Context, title: String, alertText: String, notificationId: Int) {

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_add_alert) // Ensure you have this drawable in your project
            .setContentTitle(title)
            .setContentText(alertText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())


    }

    fun isNotificationActive(context: Context, notificationId: Int): Boolean {
        val notificationManager = NotificationManagerCompat.from(context)
        val activeNotifications = notificationManager.activeNotifications
        return activeNotifications.any { it.id == notificationId }
    }

    fun updateNotification(context: Context, title: String, message: String, notificationId: Int) {
        // Cancel the existing notification
        cancelNotification(context, notificationId)

        // Show the updated notification
        NotificationUtils.showNotification(context, title, message, notificationId)
    }

    fun cancelNotification(context: Context, notificationId: Int) {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(notificationId)
    }
}