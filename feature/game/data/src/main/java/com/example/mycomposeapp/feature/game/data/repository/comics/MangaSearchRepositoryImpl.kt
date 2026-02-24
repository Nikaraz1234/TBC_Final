package com.example.mycomposeapp.feature.game.data.repository.comics

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.feature.game.data.mapper.comics.toSearchResult
import com.example.mycomposeapp.feature.game.data.remote.comics.MalApiService
import com.example.mycomposeapp.feature.game.domain.model.SearchResult
import com.example.mycomposeapp.feature.game.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MangaSearchRepositoryImpl @Inject constructor(
    private val malApiService: MalApiService
) : SearchRepository {

    override fun search(query: String): Flow<Resource<List<SearchResult>>> = flow {
        val q = query.trim()
        if (q.isBlank()) {
            emit(Resource.Success(emptyList()))
            return@flow
        }

        emit(Resource.Loading)
        try {
            val response = malApiService.searchManga(q, limit = 8)
            val results = response.data.take(8).map { it.node.toSearchResult() }
            emit(Resource.Success(results))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Search failed"))
        }
    }
}
