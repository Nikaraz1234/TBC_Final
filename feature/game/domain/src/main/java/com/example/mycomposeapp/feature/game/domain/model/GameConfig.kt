package com.example.mycomposeapp.feature.game.domain.model

data class GameConfig(
    val categoryType: String,
    val gameModeId: String,
    val questionCount: Int = 10
)
