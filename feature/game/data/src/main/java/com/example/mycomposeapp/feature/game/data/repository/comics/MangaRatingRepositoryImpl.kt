package com.example.mycomposeapp.feature.game.data.repository.comics

import com.example.mycomposeapp.feature.game.data.remote.comics.MalApiService
import com.example.mycomposeapp.feature.game.domain.model.MangaItem
import com.example.mycomposeapp.feature.game.domain.model.MangaPair
import com.example.mycomposeapp.feature.game.domain.repository.MangaRatingRepository
import javax.inject.Inject

class MangaRatingRepositoryImpl @Inject constructor(
    private val malApiService: MalApiService
) : MangaRatingRepository {

    override suspend fun fetchRandomMangaPairs(count: Int, excludeIds: Set<Long>): List<MangaPair> {
        val offset = (0..500).random()
        val response = malApiService.getMangaRanking(limit = count * 4, offset = offset)

        val valid = response.data.map { it.node }
            .filter { it.mean != null && it.mean > 0 && !excludeIds.contains(it.id) }
            .map { MangaItem(it.id, it.title, it.mainPicture?.large ?: "", it.mean ?: 0.0) }

        if (valid.size < 2) throw Exception("Not enough manga")

        val shuffled = valid.shuffled()
        return (0 until minOf(count, shuffled.size / 2)).map { i ->
            val a = shuffled[i * 2]
            val b = shuffled[i * 2 + 1]
            MangaPair(a, b)
        }.filter { it.mangaA.rating != it.mangaB.rating }
    }
}
