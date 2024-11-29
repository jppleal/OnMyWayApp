package com.jppleal.onmywayapp

class NotificationService(private val api: FcmApi) {

    suspend fun sendAlert(title: String, message: String) {
        val message = SendMessageDto(
            to = null,
            notification = NotificationBody(title, message)
        )
        api.broadcast(message)
    }
}