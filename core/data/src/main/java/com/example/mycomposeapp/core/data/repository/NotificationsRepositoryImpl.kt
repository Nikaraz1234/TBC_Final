package com.example.mycomposeapp.core.data.repository

import com.example.mycomposeapp.core.data.local.dao.NotificationsDao
import com.example.mycomposeapp.core.data.mapper.toDomain
import com.example.mycomposeapp.core.data.mapper.toEntity
import com.example.mycomposeapp.core.domain.model.AppNotification
import com.example.mycomposeapp.core.domain.repository.NotificationsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotificationsRepositoryImpl @Inject constructor(
    private val dao: NotificationsDao
) : NotificationsRepository {

    override fun observeAll(userId: String): Flow<List<AppNotification>> =
        dao.observeAll(userId).map { it.map { e -> e.toDomain() } }

    override fun observeUnread(userId: String): Flow<List<AppNotification>> =
        dao.observeUnread(userId).map { it.map { e -> e.toDomain() } }

    override suspend fun insert(notification: AppNotification) {
        dao.upsert(notification.toEntity())
    }

    override suspend fun markAsRead(id: String, userId: String) {
        dao.markRead(id, userId)
    }
}