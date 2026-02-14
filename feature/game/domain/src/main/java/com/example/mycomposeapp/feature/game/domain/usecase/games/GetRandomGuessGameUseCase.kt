package com.example.mycomposeapp.feature.game.domain.usecase.games

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.feature.game.domain.model.game.GameScreenshot
import com.example.mycomposeapp.feature.game.domain.repository.games.GamesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRandomGuessGameUseCase @Inject constructor(
    private val repository: GamesRepository
) {
    operator fun invoke(): Flow<Resource<GameScreenshot>> {
        return repository.getRandomGuessGame()
    }
}
