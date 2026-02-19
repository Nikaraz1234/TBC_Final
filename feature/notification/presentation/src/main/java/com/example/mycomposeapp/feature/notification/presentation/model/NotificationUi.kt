package com.example.mycomposeapp.feature.notification.presentation.model

data class NotificationUi(
    val id: String,
    val title: String,
    val body: String,
    val deeplink: String?,
    val type: String?,
    val date: String,
    val isRead: Boolean
)
