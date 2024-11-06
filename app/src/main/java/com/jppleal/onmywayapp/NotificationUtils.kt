package com.jppleal.onmywayapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationUtils {

    private const val CHANNEL_ID = "1"
    private const val CHANNEL_NAME = "General Notifications"
    private const val CHANNEL_DESCRIPTION = "Notifications for general purposes"

    // Call this method from your Application class or MainActivity to setup the notification channel
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Show a simple notification
    fun showNotification(context: Context, title: String, alertText: String, notificationId: Int) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_add_alert) // Ensure you have this drawable in your project
            .setContentTitle(title)
            .setContentText(alertText)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Adjust priority based on importance
            .setAutoCancel(true) // Automatically dismiss the notification when clicked

        // Show the notification
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }

    // Check if a notification is currently active
    fun isNotificationActive(context: Context, notificationId: Int): Boolean {
        val notificationManager = NotificationManagerCompat.from(context)
        val activeNotifications = notificationManager.activeNotifications
        return activeNotifications.any { it.id == notificationId }
    }

    // Update an existing notification
    fun updateNotification(context: Context, title: String, message: String, notificationId: Int) {
        // Cancel the existing notification first
        cancelNotification(context, notificationId)

        // Show the updated notification
        showNotification(context, title, message, notificationId)
    }

    // Cancel a specific notification
    fun cancelNotification(context: Context, notificationId: Int) {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(notificationId)
    }
}
