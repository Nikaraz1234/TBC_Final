package com.example.mycomposeapp.feature.game.domain.usecase.games

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.feature.game.domain.model.games.GameScreenshot
import com.example.mycomposeapp.feature.game.domain.repository.games.GamesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchGamesUseCase @Inject constructor(
    private val gamesRepository: GamesRepository
) {
    operator fun invoke(search: String): Flow<Resource<List<GameScreenshot>>> {
        val query = search.trim()
        if (query.isEmpty()) {
            return kotlinx.coroutines.flow.flowOf(Resource.Success(emptyList()))
        }
        return gamesRepository.searchGames(query)
    }
}