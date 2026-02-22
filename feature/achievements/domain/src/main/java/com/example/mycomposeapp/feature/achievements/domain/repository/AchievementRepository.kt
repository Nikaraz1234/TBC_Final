package com.example.mycomposeapp.feature.achievements.domain.repository

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.feature.achievements.domain.model.AppAchievement
import kotlinx.coroutines.flow.Flow

interface AchievementRepository {
    fun getAchievements(): Flow<Resource<List<AppAchievement>>>
    suspend fun seedAchievements()
}
