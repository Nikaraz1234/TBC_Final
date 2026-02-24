package com.example.mycomposeapp.feature.game.data.remote.comics.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MalMangaSearchResponse(
    val data: List<Item>
) {
    @Serializable
    data class Item(
        val node: Node
    )

    @Serializable
    data class Node(
        val id: Long,
        val title: String,
        @SerialName("main_picture")
        val mainPicture: MainPicture? = null,
        val mean: Double? = null
    )

    @Serializable
    data class MainPicture(
        val large: String? = null,
        val medium: String? = null
    )
}