package com.example.mycomposeapp.core.data.di

import com.example.mycomposeapp.core.data.repository.DailyGoalRepositoryImpl
import com.example.mycomposeapp.core.domain.repository.DailyGoalRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DailyGoalModule {

    @Binds
    @Singleton
    abstract fun bindDailyGoalRepository(impl: DailyGoalRepositoryImpl): DailyGoalRepository
}
