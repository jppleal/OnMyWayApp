package com.jppleal.onmywayapp

import retrofit2.http.Body
import retrofit2.http.POST

interface FcmApi {
    @POST("/send")
    suspend fun sendMessage(
         @Body body: SendMessageDto
    )
    //in this case, the send method is use to send to a specific user (like if it was a direct message such as whatsapp)
    //the broadcast method is used to send to all users in the app (not sure
    @POST("/broadcast")
    suspend fun broadcast(
        @Body body: SendMessageDto
    )
}