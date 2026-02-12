package com.example.mycomposeapp.feature.game.domain.usecase

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.repository.CoverGameRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchCoverBatchUseCase @Inject constructor(
    private val coverGameRepository: CoverGameRepository
) {
    operator fun invoke(maxPage: Int, batchSize: Int, excludeIds: Set<Int>): Flow<Resource<List<Question>>> {
        return coverGameRepository.fetchCoverBatch(maxPage, batchSize, excludeIds)
    }
}
