package com.example.mycomposeapp.feature.game.domain.model.game

data class GameScreenshot(
    val id: Long,
    val name: String,
    val screenshotUrls: List<String>
)
