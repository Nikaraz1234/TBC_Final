package com.example.mycomposeapp.feature.game.data.remote.games.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class SteamSearchResponse(
    val desc: String = "",
    val items: List<SteamSearchItemDto>? = null
)

@Serializable
data class SteamSearchItemDto(
    val name: String = "",
    val logo: String = ""
) {
    @Transient
    val appId: Int = Regex("""/apps/(\d+)/""").find(logo)?.groupValues?.get(1)?.toIntOrNull() ?: 0
}
