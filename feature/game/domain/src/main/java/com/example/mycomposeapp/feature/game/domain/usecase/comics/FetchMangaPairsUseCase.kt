package com.example.mycomposeapp.feature.game.domain.usecase.comics

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.feature.game.domain.model.comics.MangaPair
import com.example.mycomposeapp.feature.game.domain.repository.comics.MangaRatingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FetchMangaPairsUseCase @Inject constructor(
    private val repository: MangaRatingRepository
) {
    operator fun invoke(count: Int, excludeIds: Set<Long>): Flow<Resource<List<MangaPair>>> = flow {
        emit(Resource.Loading)
        try {
            val pairs = repository.fetchRandomMangaPairs(count, excludeIds)
            emit(Resource.Success(pairs))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch manga pairs"))
        }
    }
}
