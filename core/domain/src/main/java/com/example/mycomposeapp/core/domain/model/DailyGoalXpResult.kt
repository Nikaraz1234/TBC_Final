package com.example.mycomposeapp.core.domain.model

data class DailyGoalXpResult(
    val newlyCompletedGoalIds: List<String> = emptyList(),
    val xpAwarded: Int = 0,
    val allGoalsCompleted: Boolean = false,
    val bonusXpAwarded: Int = 0
) {
    val totalXpAwarded: Int get() = xpAwarded + bonusXpAwarded
    val hasAnyXp: Boolean get() = totalXpAwarded > 0
}
