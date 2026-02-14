package com.example.mycomposeapp.feature.game.domain.usecase.games

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.repository.games.GamesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchScreenshotBatchUseCase @Inject constructor(
    private val repository: GamesRepository
) {
    operator fun invoke(
        batchSize: Int,
        seenIds: Set<String>
    ): Flow<Resource<List<Question>>> =
        repository.getScreenshotQuestionBatch(batchSize, seenIds)
}