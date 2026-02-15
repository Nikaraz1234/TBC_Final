package com.example.mycomposeapp.feature.game.domain.model.game

data class Achievement(
    val name: String,
    val description: String,
    val iconUrl: String,
    val unlockPercentage: Float = 0f
)
