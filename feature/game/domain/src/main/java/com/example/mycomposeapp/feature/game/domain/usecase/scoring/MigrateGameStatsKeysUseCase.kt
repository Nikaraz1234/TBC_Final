package com.example.mycomposeapp.feature.game.domain.usecase.scoring

import com.example.mycomposeapp.core.domain.model.GameModeIds
import com.example.mycomposeapp.core.domain.repository.UserRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class MigrateGameStatsKeysUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        val user = userRepository.getCurrentUser().firstOrNull() ?: return
        val stats = user.stats

        val oldToNew = mapOf(
            "movies_cover" to GameModeIds.statsKey("MOVIES", GameModeIds.COVER),
            "movies_emoji" to GameModeIds.statsKey("MOVIES", GameModeIds.EMOJI),
            "movies_plot" to GameModeIds.statsKey("MOVIES", GameModeIds.PLOT)
        )

        val needsMigration = stats.highScore.keys.any { it in oldToNew } ||
                stats.currentStreak.keys.any { it in oldToNew }
        if (!needsMigration) return

        val migratedHighScore = stats.highScore.mapKeys { (key, _) -> oldToNew[key] ?: key }
        val migratedCurrentStreak = stats.currentStreak.mapKeys { (key, _) -> oldToNew[key] ?: key }
        userRepository.updateUserStats(
            stats.copy(
                highScore = migratedHighScore,
                currentStreak = migratedCurrentStreak
            )
        )
    }
}
