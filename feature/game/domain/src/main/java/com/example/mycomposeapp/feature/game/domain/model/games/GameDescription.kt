package com.example.mycomposeapp.feature.game.domain.model.games

data class GameDescription(
    val id: Long,
    val name: String,
    val description: String,
    val studio: String?,
    val genres: List<String>,
    val releaseYear: Int?
)
