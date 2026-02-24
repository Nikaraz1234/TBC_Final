package com.example.mycomposeapp.core.domain.usecase.daily

import com.example.mycomposeapp.core.domain.constants.DailyGoalsConstants
import com.example.mycomposeapp.core.domain.model.DailyGoal
import com.example.mycomposeapp.core.domain.model.DailyGoalsAllProgress
import com.example.mycomposeapp.core.domain.model.DailyGoalsProgress
import com.example.mycomposeapp.core.domain.repository.DailyGoalRepository
import javax.inject.Inject

class GetDailyGoalsUseCase @Inject constructor(
    private val repository: DailyGoalRepository
) {
    suspend operator fun invoke(): DailyGoalsProgress {
        repository.resetIfNewDay()
        val progress = repository.getProgress()

        val goals = listOf(
            DailyGoal(
                id = "games_played",
                title = "Play ${DailyGoalsConstants.GAMES_TO_PLAY_TARGET} games",
                currentProgress = progress.gamesPlayed,
                targetProgress = DailyGoalsConstants.GAMES_TO_PLAY_TARGET
            ),
            DailyGoal(
                id = "perfect_score",
                title = "Get a perfect score",
                currentProgress = progress.perfectScores,
                targetProgress = DailyGoalsConstants.PERFECT_SCORES_TARGET
            ),
            DailyGoal(
                id = "categories_tried",
                title = "Try ${DailyGoalsConstants.CATEGORIES_TO_TRY_TARGET} categories",
                currentProgress = progress.categoriesTried.size,
                targetProgress = DailyGoalsConstants.CATEGORIES_TO_TRY_TARGET
            ),
            DailyGoal(
                id = "game_modes_tried",
                title = "Try ${DailyGoalsConstants.GAME_MODES_TO_TRY_TARGET} different game modes",
                currentProgress = progress.gameModesTried.size,
                targetProgress = DailyGoalsConstants.GAME_MODES_TO_TRY_TARGET
            )
        )

        return DailyGoalsProgress(goals = goals)
    }

    suspend fun getAllProgress(): DailyGoalsAllProgress {
        repository.resetIfNewDay()
        val progress = repository.getProgress()

        return DailyGoalsAllProgress(
            categoriesCount = progress.categoriesTried.size,
            gameModesCount = progress.gameModesTried.size,
            categoriesTried = progress.categoriesTried.toList(),
            gameModesTried = progress.gameModesTried.toList()
        )
    }
}
