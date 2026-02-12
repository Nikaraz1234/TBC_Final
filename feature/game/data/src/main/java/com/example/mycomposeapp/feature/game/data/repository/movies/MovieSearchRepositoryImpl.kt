package com.example.mycomposeapp.feature.game.data.repository.movies

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.feature.game.data.mapper.movies.toSearchResult
import com.example.mycomposeapp.feature.game.data.remote.movies.TmdbApiService
import com.example.mycomposeapp.feature.game.domain.model.SearchResult
import com.example.mycomposeapp.feature.game.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MovieSearchRepositoryImpl @Inject constructor(
    private val tmdbApiService: TmdbApiService
) : SearchRepository {

    override fun search(query: String): Flow<Resource<List<SearchResult>>> = flow {
        emit(Resource.Loading)
        try {
            val response = tmdbApiService.searchMovies(query)
            val results = response.results.take(8).map { it.toSearchResult() }
            emit(Resource.Success(results))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Search failed"))
        }
    }
}
