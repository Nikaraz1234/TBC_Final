package com.example.mycomposeapp.core.data.mapper

import com.example.mycomposeapp.core.data.local.entity.NotificationEntity
import com.example.mycomposeapp.core.domain.model.AppNotification

fun NotificationEntity.toDomain(): AppNotification =
    AppNotification(
        id = id,
        title = title,
        body = body,
        deeplink = deeplink,
        type = type,
        createdAtMillis = createdAtMillis,
        isRead = isRead,
        userId = userId
    )

fun AppNotification.toEntity(): NotificationEntity =
    NotificationEntity(
        id = id,
        title = title,
        body = body,
        deeplink = deeplink,
        type = type,
        createdAtMillis = createdAtMillis,
        isRead = isRead,
        userId = userId
    )
 fun com.google.firebase.firestore.DocumentSnapshot.toAppNotificationOrNull(): AppNotification? {
    val id = id
    val userId = getString("userId") ?: return null
    val title = getString("title") ?: return null
    val body = getString("body") ?: ""
    val type = getString("type") ?: ""
    val deeplink = getString("deeplink") // nullable ok
    val read = getBoolean("read") ?: false
    val createdAtMillis = getLong("createdAtMillis") ?: 0L

    return AppNotification(
        id = id,
        userId = userId,
        title = title,
        body = body,
        type = type,
        deeplink = deeplink,
        isRead = read,
        createdAtMillis = createdAtMillis
    )
}

 fun AppNotification.toFirestoreMap(docId: String): Map<String, Any?> = mapOf(
    "id" to docId,
    "userId" to userId,
    "title" to title,
    "body" to body,
    "type" to type,
    "deeplink" to deeplink,
    "read" to isRead,
    "createdAtMillis" to createdAtMillis
)