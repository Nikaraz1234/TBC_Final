package com.example.mycomposeapp.ui.model

class UserUi(
    val userId: String = "",
    val username: String = "",
    val photoUrl: String = "",
    val level: Int = 1,
    val points: Int = 0,
    val stats: Stats = Stats()
){
    data class Stats(
        val coins: Int = 0,
        val gamesPlayed: Int = 0,
        val correctAnswers: Int = 0,
        val bestStreak: Int = 0,
        val achievements: List<String> = emptyList(),
        val currentStreak: Streak = Streak(),
        val highScore: HighScore = HighScore()
    ){
        data class Streak(
            val value: Int = 0
        )
        data class HighScore(
            val value: Int = 0
        )
    }
}



