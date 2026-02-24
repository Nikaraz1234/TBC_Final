package com.example.mycomposeapp.feature.game.data.mapper.games

import com.example.mycomposeapp.feature.game.data.remote.games.dto.GameDescriptionDto
import com.example.mycomposeapp.feature.game.data.remote.games.dto.GameScreenshotDto
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent
import com.example.mycomposeapp.feature.game.domain.model.SearchResult
import com.example.mycomposeapp.feature.game.domain.model.games.GameDescription
import com.example.mycomposeapp.feature.game.domain.model.games.GameScreenshot
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private const val IMAGE_BASE = "https://images.igdb.com/igdb/image/upload"

fun buildScreenshotUrl(
    imageId: String,
    size: String = "screenshot_huge"
): String = "$IMAGE_BASE/t_$size/$imageId.jpg"

fun Long?.toReleaseYear(): Int? {
    val ts = this ?: return null

    val instant = Instant.fromEpochSeconds(ts)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    return localDateTime.year
}

private fun GameScreenshotDto.extractDeveloperStudio(): String? {
    return involved_companies
        ?.firstOrNull { it.developer == true }
        ?.company
        ?.name
}

private fun GameScreenshotDto.extractGenres(): List<String> {
    return genres
        ?.mapNotNull { it.name }
        ?.distinct()
        ?: emptyList()
}

fun GameScreenshotDto.toGameScreenshot(): GameScreenshot {
    return GameScreenshot(
        id = id,
        name = name,
        screenshotUrls = screenshots?.map { buildScreenshotUrl(it.image_id) } ?: emptyList(),
        studio = extractDeveloperStudio(),
        genres = extractGenres(),
        releaseYear = this.first_release_date.toReleaseYear()
    )
}

fun GameScreenshot.toSearchResult(): SearchResult {
    val yearPart = releaseYear?.toString()
    val studioPart = studio
    val genresPart = genres.take(2).joinToString(", ").takeIf { it.isNotBlank() }

    val subtitle = listOfNotNull(studioPart, yearPart, genresPart)
        .joinToString(" • ")

    return SearchResult(
        id = id.toString(),
        title = name,
        subtitle = subtitle,
        imageUrl = screenshotUrls.firstOrNull()
    )
}


fun GameDescriptionDto.toGameDescription(): GameDescription {
    val desc = storyline?.trim().takeIf { !it.isNullOrBlank() }
        ?: summary?.trim().takeIf { !it.isNullOrBlank() }
        ?: ""


    val companies = involved_companies.orEmpty()
    val devStudio = companies.firstOrNull { it.developer == true }?.company?.name
    val anyStudio = companies.firstOrNull()?.company?.name

    return GameDescription(
        id = id,
        name = name,
        description = desc,
        studio = devStudio ?: anyStudio,
        genres = genres.orEmpty().map { it.name },
        releaseYear = first_release_date.toReleaseYear()
    )
}


fun GameDescription.toDescriptionQuestion(): Question =
    Question(
        id = "description_${id}",
        correctAnswer = name,
        content = QuestionContent.Description(
            text = description,
            studio = studio,
            genres = genres,
            releaseYear = releaseYear
        )
    )
