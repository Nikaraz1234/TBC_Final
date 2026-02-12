package com.example.mycomposeapp.feature.game.data.repository.movies

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.feature.game.data.mapper.movies.toCoverQuestion
import com.example.mycomposeapp.feature.game.data.remote.movies.TmdbApiService
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.repository.CoverGameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MovieCoverGameRepositoryImpl @Inject constructor(
    private val tmdbApiService: TmdbApiService
) : CoverGameRepository {

    override fun fetchCoverBatch(
        maxPage: Int,
        batchSize: Int,
        excludeIds: Set<String>
    ): Flow<Resource<List<Question>>> = flow {
        emit(Resource.Loading)
        try {
            val excludeIntIds = excludeIds.mapNotNull { it.toIntOrNull() }.toSet()
            val pagesToFetch = (1..maxPage).shuffled().take(3)
            val allMovies = pagesToFetch.flatMap { page ->
                tmdbApiService.getPopularMovies(page = page).results
            }
                .filter { it.posterPath != null && it.id !in excludeIntIds }
                .distinctBy { it.id }
                .shuffled()
                .take(batchSize)
                .map { it.toCoverQuestion() }

            emit(Resource.Success(allMovies))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to fetch cover questions"))
        }
    }
}
