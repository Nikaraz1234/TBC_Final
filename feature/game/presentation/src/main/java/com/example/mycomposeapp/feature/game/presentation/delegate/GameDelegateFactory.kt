package com.example.mycomposeapp.feature.game.presentation.delegate

import com.example.mycomposeapp.core.domain.model.CategoryType
import com.example.mycomposeapp.core.domain.model.GameModeIds
import com.example.mycomposeapp.core.domain.usecase.user.GetCurrentUserUseCase
import com.example.mycomposeapp.core.domain.usecase.user.UpdateCoinsUseCase
import com.example.mycomposeapp.feature.game.domain.repository.DailyPuzzleRepository
import com.example.mycomposeapp.feature.game.domain.usecase.CalculateScoreUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.FetchCoverBatchUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.GetDailyPuzzleUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.GetQuestionsUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.UpdateGameStatsUseCase
import com.example.mycomposeapp.feature.game.presentation.delegate.common.EmojiGameDelegate
import com.example.mycomposeapp.feature.game.presentation.delegate.movies.MovieCoverDelegate
import com.example.mycomposeapp.feature.game.presentation.delegate.movies.MoviePlotDelegate
import javax.inject.Inject

class GameDelegateFactory @Inject constructor(
    private val getQuestionsUseCase: GetQuestionsUseCase,
    private val calculateScoreUseCase: CalculateScoreUseCase,
    private val updateGameStatsUseCase: UpdateGameStatsUseCase,
    private val getDailyPuzzleUseCase: GetDailyPuzzleUseCase,
    private val dailyPuzzleRepositories: Map<String, @JvmSuppressWildcards DailyPuzzleRepository>,
    private val fetchCoverBatchUseCase: FetchCoverBatchUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateCoinsUseCase: UpdateCoinsUseCase
) {
    fun create(gameModeId: String, categoryType: String, archiveDate: String?): GameModeDelegate {
        return when (categoryType) {
            CategoryType.MOVIES.name -> createMoviesDelegate(gameModeId, categoryType, archiveDate)
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
                getQuestionsUseCase = getQuestionsUseCase,
                calculateScoreUseCase = calculateScoreUseCase,
                updateGameStatsUseCase = updateGameStatsUseCase
            )
            else -> throw IllegalArgumentException("Unknown game mode for $categoryType: $gameModeId")
        }
    }
}
