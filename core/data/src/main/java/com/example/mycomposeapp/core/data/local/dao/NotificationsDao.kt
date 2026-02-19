package com.example.mycomposeapp.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mycomposeapp.core.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationsDao {

    @Query("""
        SELECT * FROM notifications
        WHERE userId = :userId
        ORDER BY createdAtMillis DESC
    """)
    fun observeAll(userId: String): Flow<List<NotificationEntity>>

    @Query("""
        SELECT * FROM notifications
        WHERE userId = :userId AND isRead = 0
        ORDER BY createdAtMillis DESC
    """)
    fun observeUnread(userId: String): Flow<List<NotificationEntity>>

    @Query("""
        UPDATE notifications
        SET isRead = 1
        WHERE id = :id AND userId = :userId
    """)
    suspend fun markRead(id: String, userId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: NotificationEntity)

    @Query("DELETE FROM notifications WHERE userId = :userId")
    suspend fun clearForUser(userId: String)

    @Query("DELETE FROM notifications")
    suspend fun clearAll()
}
