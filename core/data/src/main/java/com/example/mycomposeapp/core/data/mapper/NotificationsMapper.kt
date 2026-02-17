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
        isRead = isRead
    )

fun AppNotification.toEntity(): NotificationEntity =
    NotificationEntity(
        id = id,
        title = title,
        body = body,
        deeplink = deeplink,
        type = type,
        createdAtMillis = createdAtMillis,
        isRead = isRead
    )
