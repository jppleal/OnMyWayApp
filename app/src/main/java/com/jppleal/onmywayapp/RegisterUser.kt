package com.jppleal.onmywayapp

import android.util.Log
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import java.security.MessageDigest

class RegisterUser {
    val client = createSupabaseClient(
        supabaseUrl = "https://qhhsqjvlgkdszjbsawyi.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFoaHNxanZsZ2tkc3pqYnNhd3lpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mjk1MzU5NTUsImV4cCI6MjA0NTExMTk1NX0.aRE0wZbkIpRxJsRps3Hwk1XRml5lyMNZyOQzX26FGcw"
    ){
        install(Auth)
        install(Postgrest)
    }
    suspend fun registry(remail: String, rpassword: String, callback: (Boolean) -> Unit) {
       try {
            val hashedPassword = hashPassword(rpassword)
            val result = client.auth.signUpWith(Email) {
                email = remail
                password = hashedPassword
            }
           result.run { callback(true) }
        }catch (e: Exception){
            Log.d("RegisterError", e.toString())
            callback(false)
        }
    }
     private fun hashPassword(password: String):String{
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString(""){"%02x".format(it)}
    }
}