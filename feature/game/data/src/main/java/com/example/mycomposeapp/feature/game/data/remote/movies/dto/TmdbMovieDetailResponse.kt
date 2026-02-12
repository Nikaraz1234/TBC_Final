package com.example.mycomposeapp.feature.game.data.remote.movies.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbMovieDetailResponse(
    val id: Int,
    val title: String,
    val overview: String = "",
    @SerialName("poster_path") val posterPath: String? = null,
    val tagline: String = "",
    val genres: List<TmdbGenreDto> = emptyList()
)

@Serializable
data class TmdbGenreDto(
    val id: Int,
    val name: String
)
