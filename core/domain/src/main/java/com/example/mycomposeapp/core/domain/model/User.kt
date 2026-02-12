package com.example.mycomposeapp.core.domain.model

data class User(
    val userId: String = "",
    val username: String = "",
    val photoUrl: String? = null,
    val stats: UserStats = UserStats()
)

data class UserStats(
    val level: Int = 1,
    val points: Int = 0,
    val coins: Int = 0,
    val highScore: Map<String, Int> = emptyMap(),
    val bestStreak: Int = 0,
    val currentStreak: Map<String, Int> = emptyMap(),
    val gamesPlayed: Int = 0,
    val correctAnswers: Int = 0,
    val achievements: List<String> = emptyList(),
    val lastEmojiDate: String = ""
)