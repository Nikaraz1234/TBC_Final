package com.example.mycomposeapp.feature.game.data.mapper.movies

import com.example.mycomposeapp.core.data.BuildConfig
import com.example.mycomposeapp.feature.game.data.remote.movies.dto.TmdbMovieDto
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent
import com.example.mycomposeapp.feature.game.domain.model.SearchResult

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

fun TmdbMovieDto.toSearchResult(): SearchResult {
    val imageUrl = posterPath?.let { "${BuildConfig.TMDB_IMAGE_BASE_URL}$it" }
    val year = releaseDate.take(4)
    return SearchResult(
        id = id.toString(),
        title = title,
        subtitle = year,
        imageUrl = imageUrl
    )
}
