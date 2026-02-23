package com.example.mycomposeapp.core.domain.model

data class DailyGoalRawProgress(
    val date: String = "",
    val gamesPlayed: Int = 0,
    val perfectScores: Int = 0,
    val categoriesTried: Set<String> = emptySet(),
    val gameModesTried: Set<String> = emptySet(),
    val allCompletedDate: String = ""
)

fun DailyGoalRawProgress.recordGame(
    categoryType: String,
    gameModeId: String,
    wasPerfect: Boolean
): DailyGoalRawProgress = copy(
    gamesPlayed = gamesPlayed + 1,
    perfectScores = if (wasPerfect) perfectScores + 1 else perfectScores,
    categoriesTried = categoriesTried + categoryType,
    gameModesTried = gameModesTried + "${categoryType}_${gameModeId}"
)
