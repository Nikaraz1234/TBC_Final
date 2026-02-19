package com.example.mycomposeapp.feature.notification.presentation.mapper

import com.example.mycomposeapp.core.domain.model.AppNotification
import com.example.mycomposeapp.feature.notification.presentation.model.NotificationUi
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Long.toFormattedDateTime(): String {
    val localDateTime = Instant
        .fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault())

    val hour = localDateTime.hour.toString().padStart(2, '0')
    val minute = localDateTime.minute.toString().padStart(2, '0')
    val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
    val month = localDateTime.monthNumber.toString().padStart(2, '0')
    val year = localDateTime.year

    return "$hour:$minute $day/$month/$year"
}

fun AppNotification.toPresentation(): NotificationUi =
    NotificationUi(
        id = this.id,
        title = title,
        body = body,
        deeplink = deeplink,
        type = type,
        date = createdAtMillis.toFormattedDateTime(),
        isRead = isRead
    )
