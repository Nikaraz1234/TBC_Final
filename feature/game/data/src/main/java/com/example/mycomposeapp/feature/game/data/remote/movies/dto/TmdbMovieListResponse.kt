package com.example.mycomposeapp.feature.game.data.remote.movies.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbMovieListResponse(
    val page: Int,
    val results: List<TmdbMovieDto>,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int
)
