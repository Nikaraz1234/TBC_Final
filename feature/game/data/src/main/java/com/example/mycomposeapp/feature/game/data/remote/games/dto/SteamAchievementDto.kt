package com.example.mycomposeapp.feature.game.data.remote.games.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SteamGameSchemaResponse(
    val game: SteamGameSchema? = null
)

@Serializable
data class SteamGameSchema(
    val gameName: String? = null,
    val availableGameStats: SteamGameStats? = null
)

@Serializable
data class SteamGameStats(
    val achievements: List<SteamAchievementDto>? = null
)

@Serializable
data class SteamAchievementDto(
    val name: String = "",
    val displayName: String? = null,
    val description: String? = null,
    val icon: String? = null,
    val icongray: String? = null
)

@Serializable
data class SteamAchievementPercentageResponse(
    val achievementpercentages: SteamAchievementPercentages? = null
)

@Serializable
data class SteamAchievementPercentages(
    val achievements: List<SteamAchievementPercentageDto>? = null
)

@Serializable
data class SteamAchievementPercentageDto(
    val name: String = "",
    val percent: Float = 0f
)
