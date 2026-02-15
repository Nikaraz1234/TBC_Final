package com.example.mycomposeapp.feature.game.domain.repository.games

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.model.game.GameScreenshot
import kotlinx.coroutines.flow.Flow

interface GamesRepository {
    fun searchGames(search: String): Flow<Resource<List<GameScreenshot>>>
    fun getRandomGuessGame(): Flow<Resource<GameScreenshot>>
    fun getRandomScreenshotQuestion(): Flow<Resource<Question>>

    fun getScreenshotQuestionBatch(
        batchSize: Int,
        seenIds: Set<String>
    ): Flow<Resource<List<Question>>>

    fun getDescriptionQuestionBatch(
        batchSize: Int,
        seenIds: Set<String>
    ): Flow<Resource<List<Question>>>

}