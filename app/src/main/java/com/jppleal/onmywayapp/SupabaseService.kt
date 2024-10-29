package com.jppleal.onmywayapp

import com.jppleal.onmywayapp.data.model.Alert
import okhttp3.Call
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.io.IOException

class SupabaseService {
    private val client = OkHttpClient()
    private val supabaseUrl = "https://qhhsqjvlgkdszjbsawyi.supabase.co"
    private val supabaseKey =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFoaHNxanZsZ2tkc3pqYnNhd3lpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mjk1MzU5NTUsImV4cCI6MjA0NTExMTk1NX0.aRE0wZbkIpRxJsRps3Hwk1XRml5lyMNZyOQzX26FGcw"

    fun insertAlert(alert: Alert, callback: (Boolean) -> Unit) {
        val url = "$supabaseUrl/rest/v1/alerts"
        val jsonBody = JSONObject().put("message", alert.message)
            .put("date_time", alert.dateTime)
            .toString()
        val body = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())
        val request = okhttp3.Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $supabaseKey")
            .addHeader("Content-type", "application/json")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.isSuccessful)
            }
        })
    }

    fun fetchAlerts(callback: (List<Alert>) -> Unit) {
        val url = "$supabaseUrl/rest/v1/alerts"
        val request = okhttp3.Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $supabaseKey")
            .addHeader("Content-Type", "application/json")
            .get()
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(emptyList())
            }

            override fun onResponse(call: Call, response: Response) {
                val alerts = if (response.isSuccessful) {
                    val jsonArray = JSONObject(response.body?.string()).getJSONArray("data")
                    List(jsonArray.length()) { index ->
                        val jsonAlert = jsonArray.getJSONObject(index)
                        Alert(
                            id = jsonAlert.getInt("id"),
                            message = jsonAlert.getString("message"),
                            dateTime = jsonAlert.getLong("date_time")
                        )
                    }
                } else null
                alerts?.let { callback(it) }
            }
        })
    }

    fun startListeningForAlerts(callback: (List<Alert>) -> Unit) {
        val url = "wss://qhhsqjvlgkdszjbsawyi.supabase.co/rest/v1/alerts?apikey=$supabaseKey"

        val request = Request.Builder().url(url).build()
        val webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                val joinMessage =
                    JSONObject()
                        .put("topic", "realtime:public:alerts")
                        .put("event", "phx_join")
                        .put("payload", JSONObject())
                        .put("ref", 1)
                webSocket.send(joinMessage.toString())
            }
            override fun onMessage(webSocket: WebSocket, text: String) {
                val jsonObject = JSONObject(text)
                val payload = jsonObject.optJSONObject("payload")
                payload?.let {
                    val alert = Alert(
                        id = it.getInt("id"),
                        message = it.getString("message"),
                        dateTime = it.getLong("date_time")

                    )
                    callback(listOf(alert))
                }
            }
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                t.printStackTrace()
            }
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {

            }
        }

        client.newWebSocket(request, webSocketListener)
        client.dispatcher.executorService.shutdown()
    }
}