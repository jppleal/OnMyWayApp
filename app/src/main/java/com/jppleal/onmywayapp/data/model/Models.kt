package com.jppleal.onmywayapp.data.model

import com.jppleal.onmywayapp.NotificationIdGenerator

data class Alert(
    val firebaseKey: String = "",
    val id: Int = NotificationIdGenerator.getNextNotificationId(),
    val message: String = "",
    val dateTime: Long = 0L,
    val firefighters: Int? = 0,
    val graduated: Int? = 0,
    val truckDriver: Int? = 0,
    val responded: Map<String, Response>? = null
)

data class Response(
    val status: Boolean = false,
    val estimatedTime: Int? = null
)

data class User(
    val internalNumber: Int,
    val username: String,
    val email: String,
    val emitterUser: Boolean,
    val cbNumber: CB,
    val functions: List<UserFunction>
)

enum class UserFunction{
    GRADUATED,
    TAS,
    OPTEL,
    LIGHTDRIVER,
    TRUCKDRIVER
}

data class CB(
    val cbNumber: Int
)