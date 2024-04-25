package com.jppleal.onmywayapp.data.model

data class Alert(
    val id: Int,
    val message: String,
    val dateTime: String?,
    val firefigthers: Int?,
    val graduateds: Int?,
    val truckDriver: Int?
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

data class Career(
    val careerPosition: String
)
data class CB(
    val cbNumber: Int
)