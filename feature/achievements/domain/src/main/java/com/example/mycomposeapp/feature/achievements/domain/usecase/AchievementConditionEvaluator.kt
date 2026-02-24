package com.example.mycomposeapp.feature.achievements.domain.usecase

import com.example.mycomposeapp.core.domain.model.UserStats
import com.example.mycomposeapp.feature.achievements.domain.model.AchievementConditionType
import com.example.mycomposeapp.feature.achievements.domain.model.AppAchievement

object AchievementConditionEvaluator {
    fun isMet(achievement: AppAchievement, stats: UserStats): Boolean {
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
