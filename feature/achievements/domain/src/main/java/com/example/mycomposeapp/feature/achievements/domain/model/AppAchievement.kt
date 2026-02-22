package com.example.mycomposeapp.feature.achievements.domain.model

data class AppAchievement(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val category: AchievementCategory,
    val xpReward: Int,
    val conditionType: AchievementConditionType,
    val conditionValue: Int,
    val conditionKey: String? = null
)
