package com.example.mycomposeapp.core.data.di

import android.content.Context
import androidx.room.Room
import com.example.mycomposeapp.core.data.local.NotificationsDatabase
import com.example.mycomposeapp.core.data.local.dao.NotificationsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationsDbModule {

    @Provides
    @Singleton
    fun provideNotificationsDb(
        @ApplicationContext context: Context
    ): NotificationsDatabase {
        return Room.databaseBuilder(
            context,
            NotificationsDatabase::class.java,
            "notifications.db"
        ).build()
    }

    @Provides
    fun provideNotificationsDao(db: NotificationsDatabase): NotificationsDao =
        db.notificationsDao()
}