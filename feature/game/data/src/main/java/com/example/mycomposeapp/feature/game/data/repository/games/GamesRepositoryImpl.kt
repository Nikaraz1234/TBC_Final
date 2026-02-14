package com.example.mycomposeapp.feature.game.data.repository.games

import com.example.mycomposeapp.core.data.common.HandleResponse
import com.example.mycomposeapp.core.data.common.extension.asResource
import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.feature.game.data.mapper.games.toGameScreenshot
import com.example.mycomposeapp.feature.game.data.remote.games.helper.igdbQueryBody
import com.example.mycomposeapp.feature.game.data.remote.games.query.GamesQueryBuilder
import com.example.mycomposeapp.feature.game.data.remote.games.service.GamesService
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent
import com.example.mycomposeapp.feature.game.domain.model.game.GameScreenshot
import com.example.mycomposeapp.feature.game.domain.repository.games.GamesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GamesRepositoryImpl @Inject constructor(
    private val remote: GamesService,
    private val handleResponse: HandleResponse
) : GamesRepository {

    override fun searchGames(search: String): Flow<Resource<List<GameScreenshot>>> {
        val queryString = GamesQueryBuilder.searchGames(search = search)
        val body = igdbQueryBody(queryString)

        return handleResponse
            .safeApiCall { remote.searchGames(body) }
            .map { resource ->
                resource.asResource { dtoList ->
                    dtoList.map { it.toGameScreenshot() }
                }
            }
    }
    override fun getRandomGuessGame(): Flow<Resource<GameScreenshot>> {
        val offset = (0..500).random()
        val query = GamesQueryBuilder.popularGamesWithScreenshots(limit = 50, offset = offset)
        val body = igdbQueryBody(query)

        return handleResponse.safeApiCall { remote.searchGames(body) }
            .map { res ->
                res.asResource { dtoList ->
                    val candidates = dtoList
                        .map { it.toGameScreenshot() }
                        .filter { it.screenshotUrls.isNotEmpty() }

                    candidates.random()
                }
            }
    }

    override fun getRandomScreenshotQuestion(): Flow<Resource<Question>> {
        val offset = (0..500).random()
        val query = GamesQueryBuilder.popularGamesWithScreenshots(limit = 50, offset = offset)
        val body = igdbQueryBody(query)

        return handleResponse.safeApiCall { remote.searchGames(body) }
            .map { res ->
                res.asResource { dtoList ->
                    val candidates = dtoList
                        .map { it.toGameScreenshot() }
                        .filter { it.screenshotUrls.isNotEmpty() }

                    val picked = candidates.randomOrNull()
                        ?: throw IllegalStateException("No games with screenshots found")

                    val imageUrl = picked.screenshotUrls.random()

                    Question(
                        id = "screenshot_${picked.id}",
                        correctAnswer = picked.name,
                        content = QuestionContent.Screenshot(imageUrl = imageUrl)
                    )
                }
            }
    }
    override fun getScreenshotQuestionBatch(
        batchSize: Int,
        seenIds: Set<String>
    ): Flow<Resource<List<Question>>> {
        val offset = (0..500).random()
        val query = GamesQueryBuilder.popularGamesWithScreenshots(limit = 50, offset = offset)
        val body = igdbQueryBody(query)

        return handleResponse
            .safeApiCall { remote.searchGames(body) }
            .map { res ->
                res.asResource { dtoList ->
                    val questions = dtoList
                        .map { it.toGameScreenshot() }
                        .filter { game ->
                            game.screenshotUrls.isNotEmpty() &&
                                    game.id.toString() !in seenIds
                        }
                        .shuffled()
                        .take(batchSize)
                        .map { game ->
                            Question(
                                id = "screenshot_${game.id}",
                                correctAnswer = game.name,
                                content = QuestionContent.Screenshot(
                                    imageUrl = game.screenshotUrls.random()
                                )
                            )
                        }

                    if (questions.isEmpty()) {
                        throw IllegalStateException("No screenshot questions available (maybe all were seen or missing screenshots).")
                    }

                    questions
                }
            }
    }
}