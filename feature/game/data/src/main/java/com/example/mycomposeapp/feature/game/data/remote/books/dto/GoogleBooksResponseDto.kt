package com.example.mycomposeapp.feature.game.data.remote.books.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoogleBooksResponseDto(
    @SerialName("items") val items: List<GoogleBooksVolumeDto> = emptyList(),
    @SerialName("totalItems") val totalItems: Int = 0
)
