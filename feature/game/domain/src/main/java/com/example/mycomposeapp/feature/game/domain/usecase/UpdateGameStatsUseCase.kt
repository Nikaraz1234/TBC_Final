package com.example.mycomposeapp.feature.game.domain.usecase

import com.example.mycomposeapp.core.domain.repository.UserRepository
import com.example.mycomposeapp.core.domain.model.GameModeIds
import com.example.mycomposeapp.feature.game.domain.model.GameResult
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import javax.inject.Inject

class UpdateGameStatsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(result: GameResult, gameModeId: String, isDailyMode: Boolean) {
        val user = userRepository.getCurrentUser().firstOrNull() ?: return
        val currentStats = user.stats

        val updatedHighScore = currentStats.highScore.toMutableMap()
        val currentHigh = updatedHighScore[gameModeId] ?: 0
        if (result.totalScore > currentHigh) {
            updatedHighScore[gameModeId] = result.totalScore
        }

        val updatedCurrentStreak = currentStats.currentStreak.toMutableMap()
        var updatedLastEmojiDate = currentStats.lastEmojiDate

        if (isDailyMode && gameModeId == GameModeIds.EMOJI) {
            val today = LocalDate.now()
            val lastDate = currentStats.lastEmojiDate.takeIf { it.isNotEmpty() }?.let {
                try { LocalDate.parse(it) } catch (_: Exception) { null }
            }
            val currentModeStreak = updatedCurrentStreak[gameModeId] ?: 0

            if (lastDate != null && lastDate == today.minusDays(1)) {
                updatedCurrentStreak[gameModeId] = currentModeStreak + 1
            } else {
                updatedCurrentStreak[gameModeId] = 1
            }

            updatedLastEmojiDate = today.toString()
        } else if (isDailyMode) {
            val currentModeStreak = updatedCurrentStreak[gameModeId] ?: 0
            updatedCurrentStreak[gameModeId] = currentModeStreak + 1
        }

        val updatedBestStreak = maxOf(currentStats.bestStreak, result.bestStreak)

        val newCoins = if (gameModeId == GameModeIds.COVER) result.finalCoinBalance else currentStats.coins

        val updatedStats = currentStats.copy(
            gamesPlayed = currentStats.gamesPlayed + 1,
            correctAnswers = currentStats.correctAnswers + result.correctAnswers,
            points = currentStats.points + result.totalScore,
            bestStreak = updatedBestStreak,
            currentStreak = updatedCurrentStreak,
            highScore = updatedHighScore,
            coins = newCoins,
            lastEmojiDate = updatedLastEmojiDate
        )

        userRepository.updateUserStats(updatedStats)
    }
}
