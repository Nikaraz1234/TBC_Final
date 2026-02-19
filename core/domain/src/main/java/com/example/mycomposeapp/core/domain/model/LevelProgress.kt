package com.example.mycomposeapp.core.domain.model

data class LevelProgress(
    val level: Int,
    val totalXp: Int,
    val levelStartXp: Int,
    val nextLevelXp: Int,
    val progressToNext: Float,
    val xpIntoLevel: Int,
    val xpNeededForLevel: Int,
    val xpRemaining: Int
)