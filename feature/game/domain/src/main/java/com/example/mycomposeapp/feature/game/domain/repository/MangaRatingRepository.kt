package com.example.mycomposeapp.feature.game.domain.repository

import com.example.mycomposeapp.feature.game.domain.model.MangaPair

interface MangaRatingRepository {
    suspend fun fetchRandomMangaPairs(count: Int, excludeIds: Set<Long>): List<MangaPair>
}
