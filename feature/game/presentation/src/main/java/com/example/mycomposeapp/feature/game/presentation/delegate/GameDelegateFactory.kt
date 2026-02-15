package com.example.mycomposeapp.feature.game.presentation.delegate

import com.example.mycomposeapp.core.domain.model.CategoryType
import com.example.mycomposeapp.core.domain.model.GameModeIds
import com.example.mycomposeapp.core.domain.usecase.user.GetCurrentUserUseCase
import com.example.mycomposeapp.core.domain.usecase.user.UpdateCoinsUseCase
import com.example.mycomposeapp.feature.game.domain.repository.DailyPuzzleRepository
import com.example.mycomposeapp.feature.game.domain.usecase.FetchCoverBatchUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.FetchPlotBatchUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.GetDailyPuzzleUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.UpdateGameStatsUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.games.FetchAchievementBatchUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.games.FetchScreenshotBatchUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.games.SearchGamesUseCase
import com.example.mycomposeapp.feature.game.presentation.delegate.common.EmojiGameDelegate
import com.example.mycomposeapp.feature.game.domain.usecase.games.FetchDescriptionBatchUseCase
import com.example.mycomposeapp.feature.game.presentation.delegate.games.GameAchievementDelegate
import com.example.mycomposeapp.feature.game.presentation.delegate.games.GameDescriptionDelegate
import com.example.mycomposeapp.feature.game.presentation.delegate.games.GameScreenshotDelegate
import com.example.mycomposeapp.feature.game.presentation.delegate.movies.MovieCoverDelegate
import com.example.mycomposeapp.feature.game.presentation.delegate.movies.MoviePlotDelegate
import javax.inject.Inject

class GameDelegateFactory @Inject constructor(
    private val updateGameStatsUseCase: UpdateGameStatsUseCase,
    private val getDailyPuzzleUseCase: GetDailyPuzzleUseCase,
    private val dailyPuzzleRepositories: Map<String, @JvmSuppressWildcards DailyPuzzleRepository>,
    private val fetchCoverBatchUseCase: FetchCoverBatchUseCase,
    private val fetchPlotBatchUseCase: FetchPlotBatchUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateCoinsUseCase: UpdateCoinsUseCase,
    private val fetchScreenshotBatchUseCase: FetchScreenshotBatchUseCase,
    private val searchGamesUseCase: SearchGamesUseCase,
    private val fetchAchievementBatchUseCase: FetchAchievementBatchUseCase,
    private val fetchDescriptionBatchUseCase: FetchDescriptionBatchUseCase
) {
    fun create(gameModeId: String, categoryType: String, archiveDate: String?): GameModeDelegate {
        return when (categoryType) {
            CategoryType.MOVIES.name -> createMoviesDelegate(gameModeId, categoryType, archiveDate)
            CategoryType.GAMES.name -> createGamesDelegate(gameModeId, categoryType, archiveDate)
            else -> throw IllegalArgumentException("Unknown category: $categoryType")
        }
    }

    private fun createMoviesDelegate(
        gameModeId: String,
        categoryType: String,
        archiveDate: String?
    ): GameModeDelegate {
        return when (gameModeId) {
            GameModeIds.COVER -> MovieCoverDelegate(
                categoryType = categoryType,
                gameModeId = gameModeId,
                fetchCoverBatchUseCase = fetchCoverBatchUseCase,
                getCurrentUserUseCase = getCurrentUserUseCase,
                updateCoinsUseCase = updateCoinsUseCase,
                updateGameStatsUseCase = updateGameStatsUseCase
            )
            GameModeIds.EMOJI -> EmojiGameDelegate(
                categoryType = categoryType,
                gameModeId = gameModeId,
                archiveDate = archiveDate,
                getDailyPuzzleUseCase = getDailyPuzzleUseCase,
                dailyPuzzleRepository = dailyPuzzleRepositories[categoryType]
                    ?: throw IllegalArgumentException("No daily puzzle repository for: $categoryType"),
                getCurrentUserUseCase = getCurrentUserUseCase,
                updateCoinsUseCase = updateCoinsUseCase,
                updateGameStatsUseCase = updateGameStatsUseCase
            )
            GameModeIds.PLOT -> MoviePlotDelegate(
                categoryType = categoryType,
                gameModeId = gameModeId,
                fetchPlotBatchUseCase = fetchPlotBatchUseCase,
                getCurrentUserUseCase = getCurrentUserUseCase,
                updateCoinsUseCase = updateCoinsUseCase,
                updateGameStatsUseCase = updateGameStatsUseCase
            )
            else -> throw IllegalArgumentException("Unknown game mode for $categoryType: $gameModeId")
        }
    }
    private fun createGamesDelegate(
        gameModeId: String,
        categoryType: String,
        archiveDate: String?
    ): GameModeDelegate {
        return when (gameModeId) {
            GameModeIds.GAME_SCREENSHOT -> GameScreenshotDelegate(
                categoryType = categoryType,
                gameModeId = gameModeId,
                getCurrentUserUseCase = getCurrentUserUseCase,
                updateCoinsUseCase = updateCoinsUseCase,
                updateGameStatsUseCase = updateGameStatsUseCase,
                fetchScreenshotBatchUseCase = fetchScreenshotBatchUseCase,
                searchGamesUseCase = searchGamesUseCase
            )
            GameModeIds.GAME_ACHIEVEMENT -> GameAchievementDelegate(
                categoryType = categoryType,
                gameModeId = gameModeId,
                getCurrentUserUseCase = getCurrentUserUseCase,
                updateCoinsUseCase = updateCoinsUseCase,
                updateGameStatsUseCase = updateGameStatsUseCase,
                fetchAchievementBatchUseCase = fetchAchievementBatchUseCase
            )
            GameModeIds.GAME_DESCRIPTION -> GameDescriptionDelegate(
                categoryType = categoryType,
                gameModeId = gameModeId,
                getCurrentUserUseCase = getCurrentUserUseCase,
                updateCoinsUseCase = updateCoinsUseCase,
                updateGameStatsUseCase = updateGameStatsUseCase,
                fetchDescriptionBatchUseCase = fetchDescriptionBatchUseCase,
                searchGamesUseCase = searchGamesUseCase
            )

            else -> throw IllegalArgumentException("Unknown game mode for $categoryType: $gameModeId")
        }
    }
}
