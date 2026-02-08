package com.example.mycomposeapp.core.domain.model

data class DailyChallenge(
    val categoryName: String,
    val categoryType: String,
    val gameModeName: String,
    val gameModeId: String,
    val bonusMultiplier: Int,
    val isCompleted: Boolean
)
