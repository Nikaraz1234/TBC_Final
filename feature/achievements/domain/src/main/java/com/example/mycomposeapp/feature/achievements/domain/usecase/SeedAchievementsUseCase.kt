package com.example.mycomposeapp.feature.achievements.domain.usecase

import com.example.mycomposeapp.feature.achievements.domain.repository.AchievementRepository
import javax.inject.Inject

class SeedAchievementsUseCase @Inject constructor(
    private val repository: AchievementRepository
) {
    suspend operator fun invoke() = repository.seedAchievements()
}
