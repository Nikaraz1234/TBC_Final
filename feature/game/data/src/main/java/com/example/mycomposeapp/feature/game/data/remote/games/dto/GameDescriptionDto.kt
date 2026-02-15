package com.example.mycomposeapp.feature.game.data.remote.games.dto

import kotlinx.serialization.Serializable

@Serializable
data class GameDescriptionDto(
    val id: Long,
    val name: String,
    val summary: String? = null,
    val storyline: String? = null,
    val first_release_date: Long? = null,
    val genres: List<GenreDto>? = null,
    val involved_companies: List<InvolvedCompanyDto>? = null
) {
    @Serializable data class GenreDto(val name: String)
    @Serializable data class InvolvedCompanyDto(
        val developer: Boolean? = null,
        val company: CompanyDto? = null
    )
    @Serializable data class CompanyDto(val name: String)
}
