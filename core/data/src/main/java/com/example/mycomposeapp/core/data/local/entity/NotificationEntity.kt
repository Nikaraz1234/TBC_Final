package com.example.mycomposeapp.core.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "notifications",
    indices = [Index(value = ["userId", "createdAtMillis"])]
)

data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val body: String,
    val deeplink: String?,
    val type: String?,
    val createdAtMillis: Long,
    val isRead: Boolean,
    val userId: String
)