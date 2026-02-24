package com.example.mycomposeapp.feature.achievements.domain.model

data class CheckResult(
    val allAchievements: List<AppAchievement>,
    val unlockedIds: List<String>,
    val newlyUnlocked: List<AppAchievement>
)
