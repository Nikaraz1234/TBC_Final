package com.example.mycomposeapp.feature.game.data.remote.games.dto

import kotlinx.serialization.Serializable


@Serializable
data class GameScreenshotDto(
    val id: Long,
    val name: String,
    val screenshots: List<ScreenshotDto>?
){
    @Serializable
    data class ScreenshotDto(
        val id: Long,
        val image_id: String
    )
}