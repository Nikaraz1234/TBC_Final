package com.example.mycomposeapp.core.data.di

import com.example.mycomposeapp.core.data.network.ConnectivityMonitor
import com.example.mycomposeapp.core.domain.network.ConnectivityObserver
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindConnectivityObserver(
        impl: ConnectivityMonitor
    ): ConnectivityObserver
}