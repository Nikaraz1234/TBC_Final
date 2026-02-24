package com.example.mycomposeapp.core.domain.repository

import com.example.mycomposeapp.core.domain.model.DailyGoalRawProgress

interface DailyGoalRepository {
    suspend fun getProgress(): DailyGoalRawProgress
    suspend fun saveProgress(progress: DailyGoalRawProgress)
    suspend fun resetIfNewDay()
}
