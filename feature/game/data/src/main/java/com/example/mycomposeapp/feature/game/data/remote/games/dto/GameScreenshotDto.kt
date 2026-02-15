package com.example.mycomposeapp.feature.game.data.remote.games.dto

import kotlinx.serialization.Serializable


@Serializable
data class GameScreenshotDto(
    val id: Long,
    val name: String,
    val screenshots: List<ScreenshotDto>? = null,
    val first_release_date: Long? = null,
    val genres: List<GenreDto>? = null,
    val involved_companies: List<InvolvedCompanyDto>? = null
) {

    @Serializable
    data class ScreenshotDto(
        val id: Long,
        val image_id: String
    )

    @Serializable
    data class GenreDto(
        val id: Long,
        val name: String
    )

    @Serializable
    data class InvolvedCompanyDto(
        val id: Long,
        val developer: Boolean? = null,
        val company: CompanyDto? = null
    )

    @Serializable
    data class CompanyDto(
        val id: Long,
        val name: String
    )
}