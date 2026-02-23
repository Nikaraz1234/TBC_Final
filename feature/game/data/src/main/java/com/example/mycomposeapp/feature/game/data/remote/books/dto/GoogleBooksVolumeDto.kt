package com.example.mycomposeapp.feature.game.data.remote.books.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoogleBooksVolumeDto(
    @SerialName("id") val id: String = "",
    @SerialName("volumeInfo") val volumeInfo: VolumeInfoDto = VolumeInfoDto()
)

@Serializable
data class VolumeInfoDto(
    @SerialName("title") val title: String = "",
    @SerialName("authors") val authors: List<String> = emptyList(),
    @SerialName("description") val description: String? = null,
    @SerialName("imageLinks") val imageLinks: ImageLinksDto? = null,
    @SerialName("categories") val categories: List<String> = emptyList(),
    @SerialName("pageCount") val pageCount: Int? = null,
    @SerialName("publishedDate") val publishedDate: String? = null,
    @SerialName("publisher") val publisher: String? = null
)

@Serializable
data class ImageLinksDto(
    @SerialName("thumbnail") val thumbnail: String? = null,
    @SerialName("smallThumbnail") val smallThumbnail: String? = null
)
