package com.example.mycomposeapp.feature.game.domain.model.games

data class GameScreenshot(
    val id: Long,
    val name: String,
    val screenshotUrls: List<String>,
    val studio: String?,
    val genres: List<String>,
    val releaseYear: Int?
)
