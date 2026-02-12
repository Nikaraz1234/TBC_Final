package com.example.mycomposeapp.feature.game.domain.usecase

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.repository.CoverGameRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchCoverBatchUseCase @Inject constructor(
    private val coverGameRepositories: Map<String, @JvmSuppressWildcards CoverGameRepository>
) {
    operator fun invoke(
        categoryType: String,
        maxPage: Int,
        batchSize: Int,
        excludeIds: Set<String>
    ): Flow<Resource<List<Question>>> {
        val repo = coverGameRepositories[categoryType]
            ?: throw IllegalArgumentException("No cover game repository for category: $categoryType")
        return repo.fetchCoverBatch(maxPage, batchSize, excludeIds)
    }
}
