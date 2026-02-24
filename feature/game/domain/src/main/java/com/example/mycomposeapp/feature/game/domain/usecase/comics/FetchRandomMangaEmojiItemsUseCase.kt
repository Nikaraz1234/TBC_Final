package com.example.mycomposeapp.feature.game.domain.usecase.comics

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.feature.game.domain.model.comics.MangaItem
import com.example.mycomposeapp.feature.game.domain.repository.comics.MangaEmojiRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchRandomMangaEmojiItemsUseCase @Inject constructor(
    private val mangaEmojiRepository: MangaEmojiRepository
) {
    operator fun invoke(
        count: Int,
        excludeIds: Set<Long> = emptySet()
    ): Flow<Resource<List<MangaItem>>> {
        return mangaEmojiRepository.fetchRandomMangaItems(count, excludeIds)
    }
}