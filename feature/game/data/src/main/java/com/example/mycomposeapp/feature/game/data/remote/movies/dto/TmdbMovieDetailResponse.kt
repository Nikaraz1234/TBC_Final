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
    val genres: List<TmdbGenreDto> = emptyList(),
    @SerialName("release_date") val releaseDate: String = "",
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    val credits: TmdbCreditsDto? = null
)

@Serializable
data class TmdbGenreDto(
    val id: Int,
    val name: String
)

@Serializable
data class TmdbCreditsDto(
    val cast: List<TmdbCastMemberDto> = emptyList(),
    val crew: List<TmdbCrewMemberDto> = emptyList()
)

@Serializable
data class TmdbCastMemberDto(
    val name: String,
    val order: Int = 0
)

@Serializable
data class TmdbCrewMemberDto(
    val name: String,
    val job: String = ""
)
