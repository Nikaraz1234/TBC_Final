package com.example.mycomposeapp.feature.achievements.domain.usecase

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.core.domain.model.UserStats
import com.example.mycomposeapp.core.domain.usecase.user.UpdateUserStatsUseCase
import com.example.mycomposeapp.feature.achievements.domain.model.CheckResult
import com.example.mycomposeapp.feature.achievements.domain.repository.AchievementRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CheckAndUnlockAchievementsUseCase @Inject constructor(
    private val achievementRepository: AchievementRepository,
    private val updateUserStatsUseCase: UpdateUserStatsUseCase
) {
    suspend operator fun invoke(currentStats: UserStats): CheckResult {
        val result = achievementRepository.getAchievements().first { it !is Resource.Loading }

        val allAchievements = when (result) {
            is Resource.Success -> result.data
            else -> emptyList()
        }

        val alreadyUnlocked = currentStats.achievements.toSet()
        val newlyUnlocked = allAchievements.filter { achievement ->
            achievement.id !in alreadyUnlocked && AchievementConditionEvaluator.isMet(achievement, currentStats)
        }

        if (newlyUnlocked.isNotEmpty()) {
            val totalXpGained = newlyUnlocked.sumOf { it.xpReward }
            val updatedStats = currentStats.copy(
                achievements = (alreadyUnlocked + newlyUnlocked.map { it.id }).toList(),
                totalXp = currentStats.totalXp + totalXpGained
            )
            updateUserStatsUseCase(updatedStats)
        }

        return CheckResult(
            allAchievements = allAchievements,
            unlockedIds = (alreadyUnlocked + newlyUnlocked.map { it.id }).toList(),
            newlyUnlocked = newlyUnlocked
        )
    }
}
