package com.example.mycomposeapp.feature.game.domain.usecase.books

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.repository.books.BookSynopsisRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchBookSynopsisBatchUseCase @Inject constructor(
    private val repositories: Map<String, @JvmSuppressWildcards BookSynopsisRepository>
) {
    operator fun invoke(
        categoryType: String,
        batchSize: Int,
        startIndexMax: Int,
        excludeIds: Set<String>
    ): Flow<Resource<List<Question>>> {
        val repo = repositories[categoryType]
            ?: throw IllegalArgumentException("No book synopsis repository for category: $categoryType")
        return repo.fetchSynopsisBatch(batchSize, startIndexMax, excludeIds)
    }
}
