package com.example.mycomposeapp.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mycomposeapp.core.data.local.dao.NotificationsDao
import com.example.mycomposeapp.core.data.local.entity.NotificationEntity

@Database(
    entities = [NotificationEntity::class],
    version = 2,
    exportSchema = false
)
abstract class NotificationsDatabase : RoomDatabase() {
    abstract fun notificationsDao(): NotificationsDao
}