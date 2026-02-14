package com.example.mycomposeapp.feature.game.data.repository.games

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.core.data.common.extension.asResource
import com.example.mycomposeapp.feature.game.data.mapper.games.toSearchResult
import com.example.mycomposeapp.feature.game.domain.model.SearchResult
import com.example.mycomposeapp.feature.game.domain.repository.SearchRepository
import com.example.mycomposeapp.feature.game.domain.repository.games.GamesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GameSearchRepositoryImpl @Inject constructor(
    private val gamesRepository: GamesRepository
) : SearchRepository {

    override fun search(query: String): Flow<Resource<List<SearchResult>>> {
        return gamesRepository.searchGames(query)
            .map { resource ->
                resource.asResource { gameList ->
                    gameList.map { game ->
                        game.toSearchResult()
                    }
                }
            }
    }
}
