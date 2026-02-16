package com.example.mycomposeapp.feature.game.domain.usecase.comics

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.feature.game.domain.model.game.RankleManga
import com.example.mycomposeapp.feature.game.domain.repository.RankleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetRankleMangaUseCase @Inject constructor(
    private val repository: RankleRepository
) {
    operator fun invoke(excludeIds: Set<Long>): Flow<Resource<RankleManga>> = flow {
        emit(Resource.Loading)
        try {
            val manga = repository.getRankleManga(excludeIds)
            emit(Resource.Success(manga))
        } catch (e: Exception) {
            emit(Resource.Error("Failed to load manga: ${e.localizedMessage}"))
        }
    }
}