package com.example.mycomposeapp.feature.achievements.data.di

import com.example.mycomposeapp.feature.achievements.data.repository.AchievementRepositoryImpl
import com.example.mycomposeapp.feature.achievements.domain.repository.AchievementRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AchievementModule {

    @Binds
    @Singleton
    abstract fun bindAchievementRepository(
        impl: AchievementRepositoryImpl
    ): AchievementRepository
}
