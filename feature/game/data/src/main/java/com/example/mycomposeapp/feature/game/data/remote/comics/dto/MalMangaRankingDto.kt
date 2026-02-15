package com.example.mycomposeapp.feature.game.data.remote.comics.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MalMangaRankingResponse(val data: List<MalRankingNode>)

@Serializable
data class MalRankingNode(val node: MalMangaNode)

@Serializable
data class MalMangaNode(
    val id: Long,
    val title: String,
    @SerialName("main_picture") val mainPicture: MalPicture?,
    val mean: Double?
)

@Serializable
data class MalPicture(val medium: String, val large: String)
