package com.example.mycomposeapp.feature.game.domain.usecase.books

import com.example.mycomposeapp.feature.game.domain.repository.DailyPuzzleRepository
import javax.inject.Inject

class SeedBookByOrderPuzzlesUseCase @Inject constructor(
    private val repos: Map<String, @JvmSuppressWildcards DailyPuzzleRepository>
) {
    suspend operator fun invoke() {
        repos["BOOKS"]?.seedPuzzles()
    }
}