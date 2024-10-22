package com.jppleal.onmywayapp

import com.jppleal.onmywayapp.data.model.Alert
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.Realtime


class FecthAlerts : List<Alert> {
    suspend fun fetchAlertsFromDatabase():List<Alert>{
        val client = createSupabaseClient(
            supabaseUrl = "https://qhhsqjvlgkdszjbsawyi.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFoaHNxanZsZ2tkc3pqYnNhd3lpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mjk1MzU5NTUsImV4cCI6MjA0NTExMTk1NX0.aRE0wZbkIpRxJsRps3Hwk1XRml5lyMNZyOQzX26FGcw"
        ){
            install(Realtime)
            install(Postgrest)
        }
        return try {
            val response = client.from("alerts").select{"*"}
            if(response.countOrNull() != null){
                response.data.mapNotNull { alertData ->
                    val alertMap = alertData as? Map<String, Any>

                    alertMap?.let {
                        Alert(
                            id = it["id"] as? Int ?: return@mapNotNull null,
                            message = it["message"] as? String ?: return@mapNotNull null,
                            dateTime = it["date_time"] as? Long ?: return@mapNotNull null
                        )
                    }
                }
            } else {
                emptyList()
            }
        }catch (e: Exception){
            e.printStackTrace()
            emptyList()
        }
    }
}