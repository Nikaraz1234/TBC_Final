package com.example.mycomposeapp.core.domain.usecase.daily

import com.example.mycomposeapp.core.domain.repository.DailyGoalRepository
import javax.inject.Inject

class EnsureDailyGoalsResetUseCase @Inject constructor(
    private val repository: DailyGoalRepository
) {
    suspend fun ensureTodayReset() {
        repository.resetIfNewDay()
    }
}
