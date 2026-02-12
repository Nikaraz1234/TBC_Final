package com.example.mycomposeapp.feature.game.domain.usecase

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.feature.game.domain.model.MovieSearchResult
import com.example.mycomposeapp.feature.game.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(
    private val questionRepository: QuestionRepository
) {
    operator fun invoke(query: String): Flow<Resource<List<MovieSearchResult>>> {
        return questionRepository.searchMovies(query)
    }
}
