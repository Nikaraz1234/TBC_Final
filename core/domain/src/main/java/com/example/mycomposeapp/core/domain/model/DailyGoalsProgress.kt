package com.example.mycomposeapp.core.domain.model

data class DailyGoal(
    val id: String,
    val title: String,
    val currentProgress: Int,
    val targetProgress: Int,
    val isCompleted: Boolean = currentProgress >= targetProgress
)

data class DailyGoalsProgress(
    val goals: List<DailyGoal>,
    val completedCount: Int = goals.count { it.isCompleted },
    val totalCount: Int = goals.size
)
