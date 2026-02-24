package com.example.mycomposeapp.core.domain.repository

import com.example.mycomposeapp.core.domain.model.AppNotification
import kotlinx.coroutines.flow.Flow

interface NotificationsRepository {

    fun observeAll(userId: String): Flow<List<AppNotification>>

    fun observeUnread(userId: String): Flow<List<AppNotification>>

    suspend fun insert(notification: AppNotification)

    suspend fun markAsRead(id: String, userId: String)
    suspend fun delete(id: String, userId: String)
}