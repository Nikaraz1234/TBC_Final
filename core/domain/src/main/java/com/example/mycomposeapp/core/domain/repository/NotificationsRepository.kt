package com.example.mycomposeapp.core.domain.repository

import com.example.mycomposeapp.core.domain.model.AppNotification
import kotlinx.coroutines.flow.Flow

interface NotificationsRepository {

    fun observeAll(): Flow<List<AppNotification>>

    fun observeUnread(): Flow<List<AppNotification>>

    suspend fun insert(notification: AppNotification)

    suspend fun markAsRead(id: String)

    suspend fun clearAll()
}