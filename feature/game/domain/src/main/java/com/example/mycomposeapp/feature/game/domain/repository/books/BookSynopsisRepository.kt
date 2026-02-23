package com.example.mycomposeapp.feature.game.domain.repository.books

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.feature.game.domain.model.Question
import kotlinx.coroutines.flow.Flow

interface BookSynopsisRepository {
    fun fetchSynopsisBatch(
        batchSize: Int,
        startIndexMax: Int,
        excludeIds: Set<String>
    ): Flow<Resource<List<Question>>>
}
