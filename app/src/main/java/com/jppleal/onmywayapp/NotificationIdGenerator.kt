package com.jppleal.onmywayapp

import java.util.concurrent.atomic.AtomicInteger

object NotificationIdGenerator {
    private val atomicInteger = AtomicInteger(0)

    fun getNextNotificationId(): Int{
        return atomicInteger.incrementAndGet()
    }

}