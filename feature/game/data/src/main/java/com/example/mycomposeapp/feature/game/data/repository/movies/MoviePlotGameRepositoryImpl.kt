package com.example.mycomposeapp.feature.game.data.repository.movies

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.feature.game.data.mapper.movies.toPlotQuestion
import com.example.mycomposeapp.feature.game.data.remote.movies.TmdbApiService
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.repository.movies.PlotGameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MoviePlotGameRepositoryImpl @Inject constructor(
    private val tmdbApiService: TmdbApiService
) : PlotGameRepository {

    override fun fetchPlotBatch(
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
                .filter { it.posterPath != null && it.overview.length >= 50 && it.id !in excludeIntIds }
                .distinctBy { it.id }
                .shuffled()
                .take(batchSize * 2)

            val questions = allMovies.mapNotNull { movie ->
                try {
                    val detail = tmdbApiService.getMovieDetail(movie.id, "credits")
                    detail.toPlotQuestion()
                } catch (_: Exception) {
                    null
                }
            }.take(batchSize)

            emit(Resource.Success(questions))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to fetch plot questions"))
        }
    }
}
