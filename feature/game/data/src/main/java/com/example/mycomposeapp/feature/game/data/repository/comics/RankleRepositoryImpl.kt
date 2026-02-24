package com.example.mycomposeapp.feature.game.data.repository.comics

import com.example.mycomposeapp.feature.game.data.remote.comics.MalApiService
import com.example.mycomposeapp.feature.game.domain.model.comics.RankleManga
import com.example.mycomposeapp.feature.game.domain.repository.comics.RankleRepository
import javax.inject.Inject

class RankleRepositoryImpl @Inject constructor(
    private val malApiService: MalApiService
) : RankleRepository {

    override suspend fun getRankleManga(excludeIDs: Set<Long>): RankleManga {
        val basePoolSize = 50
        val currentPoolSize = (basePoolSize + (excludeIDs.size * 3)).coerceAtMost(4000)

        val offset = (0..currentPoolSize).random()
        val response = malApiService.getMangaRanking(
            rankingType = "bypopularity",
            limit = 20,
            offset = offset
        )

        val validManga = response.data
            .map { it.node }
            .filter { node ->
                node.mean != null && node.mean > 0 && !excludeIDs.contains(node.id)
            }
            .map { node ->
                RankleManga(
                    id = node.id,
                    title = node.title,
                    imageUrl = node.mainPicture?.large ?: node.mainPicture?.medium ?: "",
                    rating = node.mean ?: 0.0
                )
            }
            .firstOrNull()
        return validManga ?: getRankleManga(excludeIDs)
    }
}