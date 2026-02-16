package com.example.mycomposeapp.feature.game.domain.repository

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.feature.game.domain.model.MangaItem
import kotlinx.coroutines.flow.Flow

interface MangaEmojiRepository {
    fun fetchRandomMangaItems(
        count: Int,
        excludeIds: Set<Long>
    ): Flow<Resource<List<MangaItem>>>}