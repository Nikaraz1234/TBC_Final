package com.example.mycomposeapp.feature.game.domain.model.games

data class Achievement(
    val name: String,
    val description: String,
    val iconUrl: String,
    val unlockPercentage: Float = 0f
)
