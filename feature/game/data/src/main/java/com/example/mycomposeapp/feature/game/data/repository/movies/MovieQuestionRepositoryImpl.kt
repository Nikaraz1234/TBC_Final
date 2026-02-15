package com.example.mycomposeapp.feature.game.data.repository.movies

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.feature.game.data.mapper.movies.toCoverQuestion
import com.example.mycomposeapp.feature.game.data.mapper.movies.toPlotQuestion
import com.example.mycomposeapp.feature.game.data.remote.movies.TmdbApiService
import com.example.mycomposeapp.feature.game.domain.model.GameConfig
import com.example.mycomposeapp.core.domain.model.GameModeIds
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MovieQuestionRepositoryImpl @Inject constructor(
    private val tmdbApiService: TmdbApiService
) : QuestionRepository {

    override fun getQuestions(config: GameConfig): Flow<Resource<List<Question>>> = flow {
        emit(Resource.Loading)
        try {
            val page1 = tmdbApiService.getPopularMovies(page = 1)
            val page2 = tmdbApiService.getPopularMovies(page = 2)
            val allMovies = (page1.results + page2.results)
                .filter { it.posterPath != null && it.overview.isNotBlank() }
                .distinctBy { it.id }
                .shuffled()

            val questions = when (config.gameModeId) {
                GameModeIds.COVER -> {
                    allMovies.take(config.questionCount).map { it.toCoverQuestion() }
                }
                GameModeIds.PLOT -> {
                    val candidates = allMovies
                        .filter { it.overview.length >= 50 }
                        .take(config.questionCount * 2)

                    candidates.mapNotNull { movie ->
                        try {
                            val detail = tmdbApiService.getMovieDetail(movie.id, "credits")
                            detail.toPlotQuestion()
                        } catch (_: Exception) {
                            null
                        }
                    }.take(config.questionCount)
                }
                else -> {
                    allMovies.take(config.questionCount).map { it.toCoverQuestion() }
                }
            }

            emit(Resource.Success(questions))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to load questions"))
        }
    }
}
