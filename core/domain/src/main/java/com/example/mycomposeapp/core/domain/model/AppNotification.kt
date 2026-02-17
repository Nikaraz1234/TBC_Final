package com.example.mycomposeapp.core.domain.model

data class AppNotification(
    val id: String,
    val title: String,
    val body: String,
    val deeplink: String?,
    val type: String?,
    val createdAtMillis: Long,
    val isRead: Boolean
)