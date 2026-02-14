package com.example.mycomposeapp.feature.game.domain.usecase

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.repository.PlotGameRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchPlotBatchUseCase @Inject constructor(
    private val plotGameRepositories: Map<String, @JvmSuppressWildcards PlotGameRepository>
) {
    operator fun invoke(
        categoryType: String,
        maxPage: Int,
        batchSize: Int,
        excludeIds: Set<String>
    ): Flow<Resource<List<Question>>> {
        val repo = plotGameRepositories[categoryType]
            ?: throw IllegalArgumentException("No plot game repository for category: $categoryType")
        return repo.fetchPlotBatch(maxPage, batchSize, excludeIds)
    }
}
