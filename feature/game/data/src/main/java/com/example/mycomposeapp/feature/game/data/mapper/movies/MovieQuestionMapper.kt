package com.example.mycomposeapp.feature.game.data.mapper.movies

import com.example.mycomposeapp.core.data.BuildConfig
import com.example.mycomposeapp.feature.game.data.remote.movies.dto.TmdbMovieDetailResponse
import com.example.mycomposeapp.feature.game.data.remote.movies.dto.TmdbMovieDto
import com.example.mycomposeapp.feature.game.domain.model.PlotHint
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

fun TmdbMovieDetailResponse.toPlotQuestion(): Question {
    val imageUrl = posterPath?.let { "${BuildConfig.TMDB_IMAGE_BASE_URL}$it" } ?: ""
    val sanitizedPlot = sanitizePlot(overview, title)

    val hints = mutableListOf<PlotHint>()

    // 1. Genres
    val genreText = genres.joinToString(", ") { it.name }
    if (genreText.isNotEmpty()) {
        hints.add(PlotHint(label = "Genres", value = genreText))
    }

    // 2. Release Year
    val year = releaseDate.take(4)
    if (year.isNotEmpty()) {
        hints.add(PlotHint(label = "Release Year", value = year))
    }

    // 3. Main Actors (top 3 by billing order)
    val topActors = credits?.cast
        ?.sortedBy { it.order }
        ?.take(3)
        ?.joinToString(", ") { it.name }
        ?: ""
    if (topActors.isNotEmpty()) {
        hints.add(PlotHint(label = "Main Actors", value = topActors))
    }

    // 4. Director
    val director = credits?.crew
        ?.firstOrNull { it.job.equals("Director", ignoreCase = true) }
        ?.name ?: ""
    if (director.isNotEmpty()) {
        hints.add(PlotHint(label = "Director", value = director))
    }

    // 5. Rating
    if (voteAverage > 0) {
        hints.add(PlotHint(label = "Rating", value = String.format("%.1f/10", voteAverage)))
    }

    // 6. Tagline
    if (tagline.isNotEmpty()) {
        hints.add(PlotHint(label = "Tagline", value = tagline))
    }

    return Question(
        id = "plot_$id",
        correctAnswer = title,
        content = QuestionContent.Plot(
            plotSummary = sanitizedPlot,
            imageUrl = imageUrl,
            hints = hints
        )
    )
}

private fun sanitizePlot(plot: String, title: String): String {
    if (title.isBlank()) return plot

    var sanitized = plot.replace(Regex(Regex.escape(title), RegexOption.IGNORE_CASE), "____")

    // For multi-word titles, also replace abbreviation (e.g. "TSR" for "The Shawshank Redemption")
    val words = title.split("\\s+".toRegex())
    if (words.size > 1) {
        val abbreviation = words.map { it.first().uppercaseChar() }.joinToString("")
        if (abbreviation.length >= 2) {
            sanitized = sanitized.replace(Regex("\\b${Regex.escape(abbreviation)}\\b"), "____")
        }
    }

    return sanitized
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
