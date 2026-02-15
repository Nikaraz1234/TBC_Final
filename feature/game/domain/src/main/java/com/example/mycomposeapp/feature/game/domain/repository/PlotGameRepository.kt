package com.example.mycomposeapp.feature.game.domain.repository

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.feature.game.domain.model.Question
import kotlinx.coroutines.flow.Flow

interface PlotGameRepository {
    fun fetchPlotBatch(maxPage: Int, batchSize: Int, excludeIds: Set<String>): Flow<Resource<List<Question>>>
}
