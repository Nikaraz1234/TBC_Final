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
    suspend operator fun invoke(
        result: GameResult,
        gameModeId: String,
        categoryType: String,
        isDailyMode: Boolean
    ) {
        val user = userRepository.getCurrentUser().firstOrNull() ?: return
        val currentStats = user.stats

        val statsKey = GameModeIds.statsKey(categoryType, gameModeId)

        val updatedHighScore = migrateKeys(currentStats.highScore).toMutableMap()
        val currentHigh = updatedHighScore[statsKey] ?: 0
        if (result.totalScore > currentHigh) {
            updatedHighScore[statsKey] = result.totalScore
        }

        val updatedCurrentStreak = migrateKeys(currentStats.currentStreak).toMutableMap()
        var updatedLastEmojiDate = currentStats.lastEmojiDate

        if (isDailyMode && gameModeId == GameModeIds.EMOJI) {
            val today = LocalDate.now()
            val lastDate = currentStats.lastEmojiDate.takeIf { it.isNotEmpty() }?.let {
                try { LocalDate.parse(it) } catch (_: Exception) { null }
            }
            val currentModeStreak = updatedCurrentStreak[statsKey] ?: 0

            if (lastDate != null && lastDate == today.minusDays(1)) {
                updatedCurrentStreak[statsKey] = currentModeStreak + 1
            } else {
                updatedCurrentStreak[statsKey] = 1
            }

            updatedLastEmojiDate = today.toString()
        } else if (isDailyMode) {
            val currentModeStreak = updatedCurrentStreak[statsKey] ?: 0
            updatedCurrentStreak[statsKey] = currentModeStreak + 1
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

    private fun migrateKeys(map: Map<String, Int>): Map<String, Int> {
        val oldToNew = mapOf(
            "movies_cover" to GameModeIds.statsKey("MOVIES", GameModeIds.COVER),
            "movies_emoji" to GameModeIds.statsKey("MOVIES", GameModeIds.EMOJI),
            "movies_plot" to GameModeIds.statsKey("MOVIES", GameModeIds.PLOT)
        )
        return map.mapKeys { (key, _) -> oldToNew[key] ?: key }
    }
}
