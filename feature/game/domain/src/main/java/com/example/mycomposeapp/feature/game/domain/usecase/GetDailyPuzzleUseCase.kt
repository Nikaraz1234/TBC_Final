package com.example.mycomposeapp.feature.game.domain.usecase

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.feature.game.domain.model.DailyPuzzle
import com.example.mycomposeapp.feature.game.domain.repository.DailyPuzzleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDailyPuzzleUseCase @Inject constructor(
    private val dailyPuzzleRepository: DailyPuzzleRepository
) {
    operator fun invoke(date: String): Flow<Resource<DailyPuzzle>> {
        return dailyPuzzleRepository.getDailyPuzzle(date)
    }
}
