package com.example.mycomposeapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannels {
    const val DAILY_TRIVIA_REMINDER = "daily_trivia_reminder"

    fun createAll(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(NotificationManager::class.java)

        val dailyTrivia = NotificationChannel(
            DAILY_TRIVIA_REMINDER,
            "Daily Trivia Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Reminders to play daily trivia and keep your streak."
        }

        manager.createNotificationChannel(dailyTrivia)
    }
}
