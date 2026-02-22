package com.example.mycomposeapp.feature.achievements.domain.usecase

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.core.domain.model.UserStats
import com.example.mycomposeapp.core.domain.usecase.user.UpdateUserStatsUseCase
import com.example.mycomposeapp.feature.achievements.domain.model.AchievementConditionType
import com.example.mycomposeapp.feature.achievements.domain.model.AppAchievement
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

        val alreadyUnlocked = currentStats.achievements.toMutableList()

        val newlyUnlocked = allAchievements.filter { achievement ->
            achievement.id !in alreadyUnlocked && isMet(achievement, currentStats)
        }

        if (newlyUnlocked.isNotEmpty()) {
            val totalXpGained = newlyUnlocked.sumOf { it.xpReward }
            val updatedStats = currentStats.copy(
                achievements = alreadyUnlocked + newlyUnlocked.map { it.id },
                totalXp = currentStats.totalXp + totalXpGained
            )
            updateUserStatsUseCase(updatedStats)
            alreadyUnlocked.addAll(newlyUnlocked.map { it.id })
        }

        return CheckResult(
            allAchievements = allAchievements,
            unlockedIds = alreadyUnlocked,
            newlyUnlocked = newlyUnlocked
        )
    }

    private fun isMet(achievement: AppAchievement, stats: UserStats): Boolean {
        return when (achievement.conditionType) {
            AchievementConditionType.GAMES_PLAYED ->
                stats.gamesPlayed >= achievement.conditionValue

            AchievementConditionType.CORRECT_ANSWERS ->
                stats.correctAnswers >= achievement.conditionValue

            AchievementConditionType.BEST_STREAK ->
                stats.bestStreak >= achievement.conditionValue

            AchievementConditionType.TOTAL_XP ->
                stats.totalXp >= achievement.conditionValue

            AchievementConditionType.LEVEL_REACHED ->
                stats.level >= achievement.conditionValue

            AchievementConditionType.POINTS_SCORED ->
                stats.points >= achievement.conditionValue

            AchievementConditionType.HIGH_SCORE -> {
                val key = achievement.conditionKey ?: return false
                (stats.highScore[key] ?: 0) >= achievement.conditionValue
            }
        }
    }
}
