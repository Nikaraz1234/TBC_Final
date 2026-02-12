package com.example.mycomposeapp.feature.game.domain.model

data class GameResult(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val totalScore: Int,
    val timeTakenSeconds: Int,
    val bestStreak: Int,
    val answers: List<AnswerResult>,
    val isNewHighScore: Boolean = false,
    val coinsEarned: Int = 0,
    val finalCoinBalance: Int = 0
)

data class AnswerResult(
    val questionId: String,
    val correctAnswer: String,
    val userAnswer: String?,
    val isCorrect: Boolean,
    val timeSpentSeconds: Int
)
