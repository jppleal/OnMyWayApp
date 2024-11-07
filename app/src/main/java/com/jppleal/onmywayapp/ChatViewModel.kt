package com.jppleal.onmywayapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.io.IOException
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

class ChatViewModel : ViewModel() {

    var state by mutableStateOf(ChatState())
        private set
    private val api: FcmApi = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/") //local host of the emulator
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create()
    init {
        viewModelScope.launch {
            Firebase.messaging.subscribeToTopic("chat").await()
        }
    }

    fun onRemoteTokenChange(newToken: String) {
        state = state.copy(remoteToken = newToken)
    }

    fun onSubmitRemoteToken() {
        state = state.copy(isEnteringToken = false)
    }

    fun onMessageChange(newMessage: String) {
        state = state.copy(messageText = newMessage)

    }

    fun sendMessage(isBroadcast: Boolean) {  //this fun sends the message to the server; This could be used when sending the alert
        viewModelScope.launch {
            val messageDto = SendMessageDto(
                to = if (isBroadcast) null else state.remoteToken,
                notification = NotificationBody(
                    title = "New message dude",
                    body = state.messageText
                )
            )
            try {
                if (isBroadcast) {
                    api.broadcast(messageDto)
                } else {
                    api.sendMessage(messageDto)
                }
                state = state.copy(messageText = "")
            }catch (e: HttpException){
                e.printStackTrace()
            }catch (w: IOException){
                w.printStackTrace()
            }
        }

    }
}