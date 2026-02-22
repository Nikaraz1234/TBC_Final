package com.example.mycomposeapp.feature.game.domain.repository.movies

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.feature.game.domain.model.Question
import kotlinx.coroutines.flow.Flow

interface PlotGameRepository {
    fun fetchPlotBatch(maxPage: Int, batchSize: Int, excludeIds: Set<String>): Flow<Resource<List<Question>>>
}
