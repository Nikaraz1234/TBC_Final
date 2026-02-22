package com.example.mycomposeapp.feature.game.domain.repository.comics

import com.example.mycomposeapp.feature.game.domain.model.comics.MangaPair

interface MangaRatingRepository {
    suspend fun fetchRandomMangaPairs(count: Int, excludeIds: Set<Long>): List<MangaPair>
}
