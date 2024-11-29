package com.jppleal.onmywayapp

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/** This file contains an object called NetworkModule that provides a unique instance (singleton) of the Retrofit, fcmApi and notificationService classes.
 * This means that these objects can easily being used anywhere in the app. without having to be recreated in the places where they are used.
 *
 * Using this object in the [OptionScreen] (for example),
 * we can call the [NetworkModule.notificationService] to obtain an instance of the [NotificationService] class.
 * This guarantees that we are using the same instance in all the app, and avoid any bugs related to multiple instances of the same class.
 */
object NetworkModule {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

/**Retrofit instance singleton **/
    val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

/**FcmApi instance singleton**/
    val fcmApi = retrofit.create(FcmApi::class.java)

 /** NotificationService instance singleton **/
    val notificationService = NotificationService(fcmApi)

}