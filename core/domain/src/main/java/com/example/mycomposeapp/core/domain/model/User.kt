package com.example.mycomposeapp.core.domain.model

data class User(
    val userId: String,
    val username: String,
    val photoUrl: String?,
    val stats: UserStats
)

data class UserStats(
    val coins: Int = 0,
    val level: Int = 1,
    val points: Int = 0,
    val gamesPlayed: Int = 0,
    val correctAnswers: Int = 0,
    val bestStreak: Int = 0,
    val currentStreak: Int = 0,
    val highScore: Int = 0,
    val achievements: Int = 0
)
