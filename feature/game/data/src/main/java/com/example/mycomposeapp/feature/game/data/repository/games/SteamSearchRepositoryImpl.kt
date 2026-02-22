package com.example.mycomposeapp.feature.game.data.repository.games

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.feature.game.data.remote.games.service.SteamStoreService
import com.example.mycomposeapp.feature.game.domain.model.SearchResult
import com.example.mycomposeapp.feature.game.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SteamSearchRepositoryImpl @Inject constructor(
    private val steamStoreService: SteamStoreService
) : SearchRepository {

    override fun search(query: String): Flow<Resource<List<SearchResult>>> = flow {
        emit(Resource.Loading)
        try {
            val response = steamStoreService.searchGamesByName(term = query)
            val results = response.items?.map { item ->
                SearchResult(
                    id = item.appId.toString(),
                    title = item.name,
                    subtitle = "",
                    imageUrl = item.logo
                )
            } ?: emptyList()
            emit(Resource.Success(results))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to search Steam games"))
        }
    }
}
