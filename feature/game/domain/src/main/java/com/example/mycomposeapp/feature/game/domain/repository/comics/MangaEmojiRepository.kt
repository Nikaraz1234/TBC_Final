package com.example.mycomposeapp.feature.game.domain.repository.comics

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.feature.game.domain.model.comics.MangaItem
import kotlinx.coroutines.flow.Flow

interface MangaEmojiRepository {
    fun fetchRandomMangaItems(
        count: Int,
        excludeIds: Set<Long>
    ): Flow<Resource<List<MangaItem>>>
}
