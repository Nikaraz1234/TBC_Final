package com.example.mycomposeapp.feature.game.data.repository.games

import com.example.mycomposeapp.core.data.BuildConfig
import com.example.mycomposeapp.core.data.common.HandleResponse
import com.example.mycomposeapp.core.data.common.extension.asResource
import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.feature.game.data.mapper.games.mergePercentages
import com.example.mycomposeapp.feature.game.data.mapper.games.steamCoverUrl
import com.example.mycomposeapp.feature.game.data.mapper.games.toAchievement
import com.example.mycomposeapp.feature.game.data.mapper.games.toDescriptionQuestion
import com.example.mycomposeapp.feature.game.data.mapper.games.toGameDescription
import com.example.mycomposeapp.feature.game.data.mapper.games.toGameScreenshot
import com.example.mycomposeapp.feature.game.data.remote.games.helper.igdbQueryBody
import com.example.mycomposeapp.feature.game.data.remote.games.query.GamesQueryBuilder
import com.example.mycomposeapp.feature.game.data.remote.games.service.GamesService
import com.example.mycomposeapp.feature.game.data.remote.games.service.SteamService
import com.example.mycomposeapp.feature.game.data.remote.games.service.SteamStoreService
import com.example.mycomposeapp.feature.game.domain.model.GameConstants.ACHIEVEMENT_BATCH_SIZE
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent
import com.example.mycomposeapp.feature.game.domain.model.game.GameScreenshot
import com.example.mycomposeapp.feature.game.domain.repository.games.GamesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GamesRepositoryImpl @Inject constructor(
    private val remote: GamesService,
    private val handleResponse: HandleResponse,
    private val steamService: SteamService,
    private val steamStoreService: SteamStoreService
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
        val query = GamesQueryBuilder.popularGamesWithScreenshots(limit = 10, offset = offset)
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
                                    imageUrl = game.screenshotUrls.random(),
                                    studio = game.studio,
                                    genres = game.genres,
                                    releaseYear = game.releaseYear
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

    override fun getAchievementQuestionBatch(
        batchSize: Int,
        seenIds: Set<String>,
        maxPages: Int
    ): Flow<Resource<List<Question>>> {
        return handleResponse.safeApiCall {
            val page = ((Math.random() * Math.random()) * maxPages).toInt().coerceIn(0, maxPages - 1)
            val start = page * ACHIEVEMENT_BATCH_SIZE
            val searchResult = steamStoreService.searchGames(start = start, count = 50)
            val candidates = (searchResult.items ?: emptyList())
                .filter { it.appId != 0 && "achievement_${it.appId}" !in seenIds }
                .shuffled()
                .take(batchSize * 3)

            val questions = mutableListOf<Question>()

            supervisorScope {
                for (candidate in candidates) {
                    if (questions.size >= batchSize) break

                    try {
                        val schemaDeferred = async {
                            steamService.getGameSchema(
                                key = BuildConfig.STEAM_API_KEY,
                                appId = candidate.appId
                            )
                        }
                        val percentagesDeferred = async {
                            steamService.getAchievementPercentages(gameId = candidate.appId)
                        }

                        val schema = schemaDeferred.await()
                        val percentages = percentagesDeferred.await()

                        val dtoAchievements = schema.game?.availableGameStats?.achievements
                            ?: continue

                        val achievements = dtoAchievements.map { it.toAchievement() }
                        val withDescriptions = achievements.filter { it.description.isNotBlank() }

                        if (withDescriptions.size < 3) continue

                        val percentageList = percentages.achievementpercentages?.achievements
                            ?: emptyList()
                        val merged = mergePercentages(
                            withDescriptions,
                            dtoAchievements.filter { (it.description ?: "").isNotBlank() },
                            percentageList
                        )

                        val gameName = candidate.name.ifBlank { schema.game.gameName ?: "" }
                        val coverUrl = steamCoverUrl(candidate.appId)

                        questions.add(
                            Question(
                                id = "achievement_${candidate.appId}",
                                correctAnswer = gameName,
                                content = QuestionContent.Achievements(
                                    achievements = merged.shuffled(),
                                    coverImageUrl = coverUrl
                                )
                            )
                        )
                    } catch (_: Exception) {
                    }
                }
            }

            if (questions.isEmpty()) {
                throw IllegalStateException("No achievement questions available.")
            }

            questions.toList()
        }
    override fun getDescriptionQuestionBatch(
        batchSize: Int,
        seenIds: Set<String>
    ): Flow<Resource<List<Question>>> {
        val offset = (0..500).random()
        val query = GamesQueryBuilder.popularGamesWithDescription(limit = 10, offset = offset)
        val body = igdbQueryBody(query)

        return handleResponse
            .safeApiCall { remote.fetchGamesByDescription(body) }
            .map { res ->
                res.asResource { dtoList ->
                    val questions = dtoList
                        .map { it.toGameDescription() }
                        .filter { game ->
                            game.description.isNotBlank() &&
                                    "description_${game.id}" !in seenIds
                        }
                        .shuffled()
                        .take(batchSize)
                        .map { game ->
                            game.toDescriptionQuestion()
                        }

                    if (questions.isEmpty()) {
                        throw IllegalStateException("No description questions available (maybe all were seen or missing description).")
                    }

                    questions
                }
            }
    }
}