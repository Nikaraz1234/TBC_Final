package com.example.mycomposeapp.feature.game.domain.usecase

import com.example.mycomposeapp.feature.game.domain.repository.DailyPuzzleRepository
import javax.inject.Inject

class SeedEmojiPuzzlesUseCase @Inject constructor(
    private val repos: Map<String, @JvmSuppressWildcards DailyPuzzleRepository>
) {
    suspend operator fun invoke() {
        repos["MOVIES"]?.seedPuzzles()
        repos["COMICS"]?.seedPuzzles()
    }
}
