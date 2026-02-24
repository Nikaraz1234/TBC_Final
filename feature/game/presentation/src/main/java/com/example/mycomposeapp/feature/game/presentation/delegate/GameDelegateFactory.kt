package com.example.mycomposeapp.feature.game.presentation.delegate

import com.example.mycomposeapp.core.domain.model.CategoryType
import com.example.mycomposeapp.core.domain.model.GameModeIds
import com.example.mycomposeapp.core.domain.usecase.daily.DailyGoalsManagerUseCase
import com.example.mycomposeapp.core.domain.usecase.daily.UpdateDailyGoalProgressUseCase
import com.example.mycomposeapp.core.domain.usecase.user.GetCurrentUserUseCase
import com.example.mycomposeapp.core.domain.usecase.user.UpdateCoinsUseCase
import com.example.mycomposeapp.core.domain.usecase.user.UpdateUserStatsUseCase
import com.example.mycomposeapp.feature.game.domain.repository.DailyPuzzleRepository
import com.example.mycomposeapp.feature.game.domain.usecase.movies.FetchCoverBatchUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.movies.FetchPlotBatchUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.GetDailyPuzzleUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.scoring.UpdateGameStatsUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.games.FetchAchievementBatchUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.games.FetchScreenshotBatchUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.games.SearchGamesUseCase
import com.example.mycomposeapp.feature.game.presentation.delegate.common.EmojiGameDelegate
import com.example.mycomposeapp.feature.game.domain.usecase.games.FetchDescriptionBatchUseCase
import com.example.mycomposeapp.feature.game.presentation.delegate.games.GameAchievementDelegate
import com.example.mycomposeapp.feature.game.presentation.delegate.comics.MangaRatingDelegate
import com.example.mycomposeapp.feature.game.presentation.delegate.games.GameDescriptionDelegate
import com.example.mycomposeapp.feature.game.presentation.delegate.games.GameScreenshotDelegate
import com.example.mycomposeapp.feature.game.presentation.delegate.movies.MovieCoverDelegate
import com.example.mycomposeapp.feature.game.presentation.delegate.movies.MoviePlotDelegate
import com.example.mycomposeapp.feature.game.domain.usecase.comics.FetchMangaPairsUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.comics.GetRankleMangaUseCase
import com.example.mycomposeapp.feature.game.presentation.delegate.books.BookByOrderDelegate
import com.example.mycomposeapp.feature.game.domain.usecase.books.FetchBookSynopsisBatchUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.books.FetchBookOddOneOutBatchUseCase
import com.example.mycomposeapp.feature.game.presentation.delegate.books.BookSynopsisDelegate
import com.example.mycomposeapp.feature.game.presentation.delegate.books.BookOddOneOutDelegate
import com.example.mycomposeapp.feature.game.presentation.delegate.comics.RankleDelegate
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
    private val fetchDescriptionBatchUseCase: FetchDescriptionBatchUseCase,
    private val fetchMangaPairsUseCase: FetchMangaPairsUseCase,
    private val getRankleMangaUseCase: GetRankleMangaUseCase,
    private val updateDailyGoalProgressUseCase: UpdateDailyGoalProgressUseCase,
    private val dailyGoalsManagerUseCase: DailyGoalsManagerUseCase,
    private val updateUserStatsUseCase: UpdateUserStatsUseCase,
    private val fetchBookSynopsisBatchUseCase: FetchBookSynopsisBatchUseCase,
    private val fetchBookOddOneOutBatchUseCase: FetchBookOddOneOutBatchUseCase
) {
    fun create(gameModeId: String, categoryType: String, archiveDate: String?): GameModeDelegate {
        return when (categoryType) {
            CategoryType.MOVIES.name -> createMoviesDelegate(gameModeId, categoryType, archiveDate)
            CategoryType.GAMES.name -> createGamesDelegate(gameModeId, categoryType, archiveDate)
            CategoryType.COMICS.name -> createComicsDelegate(gameModeId, categoryType, archiveDate)
            CategoryType.BOOKS.name -> createBooksDelegate(gameModeId, categoryType, archiveDate)
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
                updateGameStatsUseCase = updateGameStatsUseCase,
                updateDailyGoalProgressUseCase = updateDailyGoalProgressUseCase,
                dailyGoalsManagerUseCase = dailyGoalsManagerUseCase,
                updateUserStatsUseCase = updateUserStatsUseCase
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
                updateGameStatsUseCase = updateGameStatsUseCase,
                updateDailyGoalProgressUseCase = updateDailyGoalProgressUseCase,
                dailyGoalsManagerUseCase = dailyGoalsManagerUseCase,
                updateUserStatsUseCase = updateUserStatsUseCase
            )
            GameModeIds.PLOT -> MoviePlotDelegate(
                categoryType = categoryType,
                gameModeId = gameModeId,
                fetchPlotBatchUseCase = fetchPlotBatchUseCase,
                getCurrentUserUseCase = getCurrentUserUseCase,
                updateCoinsUseCase = updateCoinsUseCase,
                updateGameStatsUseCase = updateGameStatsUseCase,
                updateDailyGoalProgressUseCase = updateDailyGoalProgressUseCase,
                dailyGoalsManagerUseCase = dailyGoalsManagerUseCase,
                updateUserStatsUseCase = updateUserStatsUseCase
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
                searchGamesUseCase = searchGamesUseCase,
                updateDailyGoalProgressUseCase = updateDailyGoalProgressUseCase,
                dailyGoalsManagerUseCase = dailyGoalsManagerUseCase,
                updateUserStatsUseCase = updateUserStatsUseCase
            )
            GameModeIds.GAME_ACHIEVEMENT -> GameAchievementDelegate(
                categoryType = categoryType,
                gameModeId = gameModeId,
                getCurrentUserUseCase = getCurrentUserUseCase,
                updateCoinsUseCase = updateCoinsUseCase,
                updateGameStatsUseCase = updateGameStatsUseCase,
                fetchAchievementBatchUseCase = fetchAchievementBatchUseCase,
                updateDailyGoalProgressUseCase = updateDailyGoalProgressUseCase,
                dailyGoalsManagerUseCase = dailyGoalsManagerUseCase,
                updateUserStatsUseCase = updateUserStatsUseCase
            )
            GameModeIds.GAME_DESCRIPTION -> GameDescriptionDelegate(
                categoryType = categoryType,
                gameModeId = gameModeId,
                getCurrentUserUseCase = getCurrentUserUseCase,
                updateCoinsUseCase = updateCoinsUseCase,
                updateGameStatsUseCase = updateGameStatsUseCase,
                fetchDescriptionBatchUseCase = fetchDescriptionBatchUseCase,
                searchGamesUseCase = searchGamesUseCase,
                updateDailyGoalProgressUseCase = updateDailyGoalProgressUseCase,
                dailyGoalsManagerUseCase = dailyGoalsManagerUseCase,
                updateUserStatsUseCase = updateUserStatsUseCase
            )

            else -> throw IllegalArgumentException("Unknown game mode for $categoryType: $gameModeId")
        }
    }

    private fun createComicsDelegate(
        gameModeId: String,
        categoryType: String,
        archiveDate: String?
    ): GameModeDelegate {
        return when (gameModeId) {
            GameModeIds.MANGA_RATING -> MangaRatingDelegate(
                categoryType = categoryType,
                gameModeId = gameModeId,
                fetchMangaPairsUseCase = fetchMangaPairsUseCase,
                getCurrentUserUseCase = getCurrentUserUseCase,
                updateCoinsUseCase = updateCoinsUseCase,
                updateGameStatsUseCase = updateGameStatsUseCase,
                updateDailyGoalProgressUseCase = updateDailyGoalProgressUseCase,
                dailyGoalsManagerUseCase = dailyGoalsManagerUseCase,
                updateUserStatsUseCase = updateUserStatsUseCase
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
                updateGameStatsUseCase = updateGameStatsUseCase,
                updateDailyGoalProgressUseCase = updateDailyGoalProgressUseCase,
                dailyGoalsManagerUseCase = dailyGoalsManagerUseCase,
                updateUserStatsUseCase = updateUserStatsUseCase
            )

            GameModeIds.RANKLE -> RankleDelegate(
                categoryType = categoryType,
                gameModeId = gameModeId,
                getCurrentUserUseCase = getCurrentUserUseCase,
                updateCoinsUseCase = updateCoinsUseCase,
                updateGameStatsUseCase = updateGameStatsUseCase,
                getRankleMangaUseCase = getRankleMangaUseCase,
                updateDailyGoalProgressUseCase = updateDailyGoalProgressUseCase,
                dailyGoalsManagerUseCase = dailyGoalsManagerUseCase,
                updateUserStatsUseCase = updateUserStatsUseCase
            )
            else -> throw IllegalArgumentException("Unknown game mode for $categoryType: $gameModeId")
        }
    }

    private fun createBooksDelegate(
        gameModeId: String,
        categoryType: String
    ): GameModeDelegate {
        return when (gameModeId) {
          GameModeIds.BOOK_BY_ORDER -> BookByOrderDelegate(
                categoryType = categoryType,
                gameModeId = gameModeId,
                archiveDate = archiveDate,
                getDailyPuzzleUseCase = getDailyPuzzleUseCase,
                dailyPuzzleRepository = dailyPuzzleRepositories[categoryType]
                    ?: throw IllegalArgumentException("No daily puzzle repository for: $categoryType")
          
            GameModeIds.BOOK_SYNOPSIS -> BookSynopsisDelegate(
                categoryType = categoryType,
                gameModeId = gameModeId,
                fetchBookSynopsisBatchUseCase = fetchBookSynopsisBatchUseCase,
                getCurrentUserUseCase = getCurrentUserUseCase,
                updateCoinsUseCase = updateCoinsUseCase,
                updateGameStatsUseCase = updateGameStatsUseCase,
                updateDailyGoalProgressUseCase = updateDailyGoalProgressUseCase,
                dailyGoalsManagerUseCase = dailyGoalsManagerUseCase,
                updateUserStatsUseCase = updateUserStatsUseCase
            )
            GameModeIds.BOOK_ODD_ONE_OUT -> BookOddOneOutDelegate(
                categoryType = categoryType,
                gameModeId = gameModeId,
                fetchBookOddOneOutBatchUseCase = fetchBookOddOneOutBatchUseCase,
                getCurrentUserUseCase = getCurrentUserUseCase,
                updateCoinsUseCase = updateCoinsUseCase,
                updateGameStatsUseCase = updateGameStatsUseCase,
                updateDailyGoalProgressUseCase = updateDailyGoalProgressUseCase,
                dailyGoalsManagerUseCase = dailyGoalsManagerUseCase,
                updateUserStatsUseCase = updateUserStatsUseCase
            )
            else -> throw IllegalArgumentException("Unknown game mode for $categoryType: $gameModeId")
        }
    }
}
