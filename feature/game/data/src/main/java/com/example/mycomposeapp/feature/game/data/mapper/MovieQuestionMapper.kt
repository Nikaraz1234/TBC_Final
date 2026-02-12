package com.example.mycomposeapp.feature.game.data.mapper

import com.example.mycomposeapp.core.data.BuildConfig
import com.example.mycomposeapp.feature.game.data.remote.dto.TmdbMovieDto
import com.example.mycomposeapp.feature.game.domain.model.MovieSearchResult
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent

fun TmdbMovieDto.toCoverQuestion(): Question {
    val imageUrl = posterPath?.let { "${BuildConfig.TMDB_IMAGE_BASE_URL}$it" } ?: ""
    return Question(
        id = "cover_$id",
        correctAnswer = title,
        content = QuestionContent.Cover(imageUrl = imageUrl)
    )
}

fun TmdbMovieDto.toPlotQuestion(): Question {
    return Question(
        id = "plot_$id",
        correctAnswer = title,
        content = QuestionContent.Plot(plotSummary = overview)
    )
}

fun TmdbMovieDto.toSearchResult(): MovieSearchResult {
    val posterUrl = posterPath?.let { "${BuildConfig.TMDB_IMAGE_BASE_URL}$it" }
    val year = releaseDate.take(4)
    return MovieSearchResult(
        id = id,
        title = title,
        releaseYear = year,
        posterUrl = posterUrl
    )
}
