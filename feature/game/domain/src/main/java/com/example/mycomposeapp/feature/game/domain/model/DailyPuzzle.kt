package com.example.mycomposeapp.feature.game.domain.model

data class DailyPuzzle(
    val date: String,
    val question: Question,
    val isCompleted: Boolean,
    val isFromArchive: Boolean
)
