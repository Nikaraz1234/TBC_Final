package com.example.mycomposeapp.feature.game.domain.usecase

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.feature.game.domain.model.DailyPuzzle
import com.example.mycomposeapp.feature.game.domain.repository.DailyPuzzleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDailyPuzzleUseCase @Inject constructor(
    private val dailyPuzzleRepositories: Map<String, @JvmSuppressWildcards DailyPuzzleRepository>
) {
    operator fun invoke(categoryType: String, date: String): Flow<Resource<DailyPuzzle>> {
        val repo = dailyPuzzleRepositories[categoryType]
            ?: throw IllegalArgumentException("No daily puzzle repository for category: $categoryType")
        return repo.getDailyPuzzle(date)
    }
}
