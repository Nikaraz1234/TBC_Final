package com.example.mycomposeapp.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mycomposeapp.R
import com.example.mycomposeapp.core.domain.keys.PreferenceKeys
import com.example.mycomposeapp.core.domain.model.AppNotification
import com.example.mycomposeapp.core.domain.usecase.datastore.SetPreferenceUseCase
import com.example.mycomposeapp.core.domain.usecase.notification.SaveNotificationUseCase
import com.example.mycomposeapp.core.domain.usecase.user.GetCurrentUserUseCase
import com.example.mycomposeapp.ui.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random
@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var setPreferenceUseCase: SetPreferenceUseCase
    @Inject lateinit var saveNotification: SaveNotificationUseCase
    @Inject lateinit var getCurrentUserUseCase: GetCurrentUserUseCase


    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(message: RemoteMessage) {
        val data = message.data

        CoroutineScope(Dispatchers.IO).launch {
            val user = getCurrentUserUseCase().firstOrNull()
                ?: return@launch

            val userId = user.userId

            val type = data[KEY_TYPE]
            val deeplink = data[KEY_DEEPLINK]
            val fallbackRoute = data[KEY_FALLBACK]

            val title = message.notification?.title
                ?: data["title"]
                ?: "MyComposeApp"

            val body = message.notification?.body
                ?: data["body"]
                ?: "Tap to open"

            val targetUserId = data["userId"]
            if (targetUserId != null && targetUserId != userId) return@launch

            val appNotification = AppNotification(
                id = message.messageId ?: System.currentTimeMillis().toString(),
                title = title,
                body = body,
                deeplink = deeplink,
                type = type,
                createdAtMillis = System.currentTimeMillis(),
                isRead = false,
                userId = userId
            )

            saveNotification(appNotification)

            createChannelIfNeeded()

            val intent = Intent(this@MyFirebaseMessagingService, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(KEY_TYPE, type)
                putExtra(KEY_DEEPLINK, deeplink)
                putExtra(KEY_FALLBACK, fallbackRoute)
                if (!deeplink.isNullOrBlank()) {
                    this.data = Uri.parse(deeplink)
                }
            }

            val pendingIntent = PendingIntent.getActivity(
                this@MyFirebaseMessagingService,
                appNotification.id.hashCode(), // unique
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(
                this@MyFirebaseMessagingService,
                CHANNEL_DAILY_TRIVIA_REMINDER
            )
                .setSmallIcon(com.example.mycomposeapp.feature.main.presentation.R.drawable.ic_cover)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

            NotificationManagerCompat.from(this@MyFirebaseMessagingService)
                .notify(appNotification.id.hashCode(), notification)
        }
    }
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        CoroutineScope(Dispatchers.IO).launch {
            setPreferenceUseCase(PreferenceKeys.FCM_TOKEN, token)
        }
    }

    private fun createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = getSystemService(NotificationManager::class.java)

        val existing = manager.getNotificationChannel(CHANNEL_DAILY_TRIVIA_REMINDER)
        if (existing != null) return

        val channel = NotificationChannel(
            CHANNEL_DAILY_TRIVIA_REMINDER,
            "Daily Trivia Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Daily trivia and streak reminders"
        }

        manager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_DAILY_TRIVIA_REMINDER = "daily_trivia_reminder"
        const val KEY_TYPE = "type"
        const val KEY_DEEPLINK = "deeplink"
        const val KEY_FALLBACK = "fallbackRoute"
    }
}
