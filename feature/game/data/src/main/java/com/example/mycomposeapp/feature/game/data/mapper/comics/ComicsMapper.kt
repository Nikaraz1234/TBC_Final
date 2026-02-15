package com.example.mycomposeapp.feature.game.data.mapper.comics

import com.example.mycomposeapp.feature.game.data.remote.comics.dto.MalMangaSearchResponse
import com.example.mycomposeapp.feature.game.domain.model.SearchResult

fun MalMangaSearchResponse.Node.toSearchResult(): SearchResult {
    val score = mean
    val subtitle = if (score != null && score > 0.0) "Score: %.2f".format(score) else ""

    return SearchResult(
        id = id.toString(),
        title = title,
        subtitle = subtitle,
        imageUrl = mainPicture?.large ?: mainPicture?.medium
    )
}