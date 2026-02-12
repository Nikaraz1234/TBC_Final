package com.example.mycomposeapp.feature.game.presentation.delegate

import com.example.mycomposeapp.core.domain.usecase.user.GetCurrentUserUseCase
import com.example.mycomposeapp.core.domain.usecase.user.UpdateCoinsUseCase
import com.example.mycomposeapp.core.domain.model.GameModeIds
import com.example.mycomposeapp.feature.game.domain.repository.DailyPuzzleRepository
import com.example.mycomposeapp.feature.game.domain.usecase.CalculateScoreUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.FetchCoverBatchUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.GetDailyPuzzleUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.GetQuestionsUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.UpdateGameStatsUseCase
import javax.inject.Inject

class GameDelegateFactory @Inject constructor(
    private val getQuestionsUseCase: GetQuestionsUseCase,
    private val calculateScoreUseCase: CalculateScoreUseCase,
    private val updateGameStatsUseCase: UpdateGameStatsUseCase,
    private val getDailyPuzzleUseCase: GetDailyPuzzleUseCase,
    private val dailyPuzzleRepository: DailyPuzzleRepository,
    private val fetchCoverBatchUseCase: FetchCoverBatchUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateCoinsUseCase: UpdateCoinsUseCase
) {
    fun create(gameModeId: String, categoryType: String, archiveDate: String?): GameModeDelegate {
        return when (gameModeId) {
            GameModeIds.COVER -> CoverGameDelegate(
                gameModeId = gameModeId,
                fetchCoverBatchUseCase = fetchCoverBatchUseCase,
                getCurrentUserUseCase = getCurrentUserUseCase,
                updateCoinsUseCase = updateCoinsUseCase,
                updateGameStatsUseCase = updateGameStatsUseCase
            )
            GameModeIds.EMOJI -> EmojiGameDelegate(
                gameModeId = gameModeId,
                archiveDate = archiveDate,
                getDailyPuzzleUseCase = getDailyPuzzleUseCase,
                dailyPuzzleRepository = dailyPuzzleRepository,
                getCurrentUserUseCase = getCurrentUserUseCase,
                updateCoinsUseCase = updateCoinsUseCase,
                updateGameStatsUseCase = updateGameStatsUseCase
            )
            GameModeIds.PLOT -> PlotGameDelegate(
                gameModeId = gameModeId,
                categoryType = categoryType,
                getQuestionsUseCase = getQuestionsUseCase,
                calculateScoreUseCase = calculateScoreUseCase,
                updateGameStatsUseCase = updateGameStatsUseCase
            )

            //GameModeIds.GAMES_SCREENSHOT -> GameScreenshotDelegate()

            //GameModeIds.GAMES_DESCRIPTION -> GameDescriptionDelegate()
            else -> PlotGameDelegate(
                gameModeId = gameModeId,
                categoryType = categoryType,
                getQuestionsUseCase = getQuestionsUseCase,
                calculateScoreUseCase = calculateScoreUseCase,
                updateGameStatsUseCase = updateGameStatsUseCase
            )
        }
    }
}
