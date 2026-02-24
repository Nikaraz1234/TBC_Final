package com.example.mycomposeapp.core.data.di

import com.example.mycomposeapp.core.data.repository.NotificationsRepositoryImpl
import com.example.mycomposeapp.core.domain.repository.NotificationsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationsRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNotificationsRepository(
        impl: NotificationsRepositoryImpl
    ): NotificationsRepository
}