package com.example.mycomposeapp.feature.game.data.mapper.games

import com.example.mycomposeapp.feature.game.data.remote.games.dto.GameScreenshotDto
import com.example.mycomposeapp.feature.game.domain.model.SearchResult
import com.example.mycomposeapp.feature.game.domain.model.game.GameScreenshot

private const val IMAGE_BASE =
    "https://images.igdb.com/igdb/image/upload"

fun buildScreenshotUrl(
    imageId: String,
    size: String = "screenshot_huge"
): String {
    return "$IMAGE_BASE/t_$size/$imageId.jpg"
}


fun GameScreenshotDto.toGameScreenshot
            (): GameScreenshot {
    return GameScreenshot(
        id = id,
        name = name,
        screenshotUrls = screenshots
            ?.map { buildScreenshotUrl(it.image_id) }
            ?: emptyList()
    )
}

fun GameScreenshot.toSearchResult(): SearchResult {
    return SearchResult(
        id = id.toString(),
        title = name,
        subtitle = "",
        imageUrl = screenshotUrls.firstOrNull()
    )
}