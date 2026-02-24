package com.example.mycomposeapp.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mycomposeapp.core.domain.keys.PreferenceKeys
import com.example.mycomposeapp.core.domain.model.AppNotification
import com.example.mycomposeapp.core.domain.usecase.datastore.SetPreferenceUseCase
import com.example.mycomposeapp.core.domain.usecase.notification.SaveNotificationUseCase
import com.example.mycomposeapp.core.domain.usecase.user.GetCurrentUserUseCase
import com.example.mycomposeapp.ui.MainActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject lateinit var setPreferenceUseCase: SetPreferenceUseCase
    @Inject lateinit var saveNotification: SaveNotificationUseCase
    @Inject lateinit var getCurrentUserUseCase: GetCurrentUserUseCase

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(message: RemoteMessage) {
        val payload = message.data

        CoroutineScope(Dispatchers.IO).launch {
            val authUid = auth.currentUser?.uid
            if (authUid == null) {
                Log.w(TAG, "No FirebaseAuth user. Skipping Firestore save. (Will still show system notification)")
            }

            val user = getCurrentUserUseCase().firstOrNull()
            val appUserId = user?.userId


            val targetUserId = payload["userId"]
            if (targetUserId != null) {
                val expected = authUid ?: appUserId
                if (expected == null || targetUserId != expected) {
                    Log.d(TAG, "Message ignored (targetUserId=$targetUserId, expected=$expected)")
                    return@launch
                }
            }

            val type = payload[KEY_TYPE]
            val deeplink = payload[KEY_DEEPLINK]
            val fallbackRoute = payload[KEY_FALLBACK]

            val title = message.notification?.title
                ?: payload["title"]
                ?: "MyComposeApp"

            val body = message.notification?.body
                ?: payload["body"]
                ?: "Tap to open"

            val iconName = payload["icon"]
            val iconRes = if (!iconName.isNullOrBlank()) {
                resources.getIdentifier(iconName, "drawable", packageName)
            } else 0
            val safeIcon =
                if (iconRes != 0) iconRes else com.example.mycomposeapp.core.ui.R.drawable.ic_notification

            val notificationId = message.messageId ?: System.currentTimeMillis().toString()
            val now = System.currentTimeMillis()

            val effectiveUserIdForModel = (authUid ?: appUserId) ?: "unknown"

            val appNotification = AppNotification(
                id = notificationId,
                title = title,
                body = body,
                deeplink = deeplink,
                type = type,
                createdAtMillis = now,
                isRead = false,
                userId = effectiveUserIdForModel
            )

            runCatching { saveNotification(appNotification) }
                .onFailure { e -> Log.e(TAG, "Local save failed: ${e.message}", e) }

            if (authUid != null) {
                try {
                    firestore.collection("users")
                        .document(authUid) // IMPORTANT: uses Firebase UID so rules match request.auth.uid
                        .collection("notifications")
                        .document(notificationId)
                        .set(appNotification)
                        .awaitVoid()

                    Log.d(TAG, "Saved notification to Firestore: users/$authUid/notifications/$notificationId")
                } catch (e: Exception) {
                    Log.e(TAG, "Firestore save FAILED: ${e.message}", e)
                }
            }

            createChannelIfNeeded()

            val intent = Intent(this@MyFirebaseMessagingService, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(KEY_TYPE, type)
                putExtra(KEY_DEEPLINK, deeplink)
                putExtra(KEY_FALLBACK, fallbackRoute)

                if (!deeplink.isNullOrBlank()) {
                    // IMPORTANT: use Intent.data (not the payload map)
                    this.data = Uri.parse(deeplink)
                }
            }

            val pendingIntent = PendingIntent.getActivity(
                this@MyFirebaseMessagingService,
                notificationId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val systemNotification = NotificationCompat.Builder(
                this@MyFirebaseMessagingService,
                CHANNEL_DAILY_TRIVIA_REMINDER
            )
                .setSmallIcon(safeIcon)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

            NotificationManagerCompat.from(this@MyFirebaseMessagingService)
                .notify(notificationId.hashCode(), systemNotification)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        CoroutineScope(Dispatchers.IO).launch {
            runCatching { setPreferenceUseCase(PreferenceKeys.FCM_TOKEN, token) }
                .onFailure { e -> Log.e(TAG, "Saving token locally failed: ${e.message}", e) }

            val authUid = auth.currentUser?.uid
            if (authUid == null) {
                Log.w(TAG, "No FirebaseAuth user in onNewToken(). Token not saved remotely yet.")
                return@launch
            }

            try {
                firestore.collection("users")
                    .document(authUid)
                    .collection("fcmTokens")
                    .document(token)
                    .set(
                        mapOf(
                            "createdAtMillis" to System.currentTimeMillis(),
                            "platform" to "android"
                        )
                    )
                    .awaitVoid()

                Log.d(TAG, "Saved FCM token to Firestore: users/$authUid/fcmTokens/$token")
            } catch (e: Exception) {
                Log.e(TAG, "Firestore token save FAILED: ${e.message}", e)
            }
        }
    }

    private fun createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = getSystemService(NotificationManager::class.java)
        if (manager.getNotificationChannel(CHANNEL_DAILY_TRIVIA_REMINDER) != null) return

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
        private const val TAG = "MyFCMService"

        const val CHANNEL_DAILY_TRIVIA_REMINDER = "daily_trivia_reminder"
        const val KEY_TYPE = "type"
        const val KEY_DEEPLINK = "deeplink"
        const val KEY_FALLBACK = "fallbackRoute"
    }
}

private suspend fun <T> Task<T>.awaitResult(): T =
    suspendCancellableCoroutine { cont ->
        addOnSuccessListener { result -> cont.resume(result) }
        addOnFailureListener { e -> cont.resumeWithException(e) }
        addOnCanceledListener { cont.cancel() }
    }

private suspend fun Task<Void>.awaitVoid() {
    awaitResult()
}