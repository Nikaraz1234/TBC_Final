package com.example.mycomposeapp.feature.game.domain.usecase.books

import com.example.mycomposeapp.feature.game.domain.repository.DailyPuzzleRepository
import javax.inject.Inject
import javax.inject.Named

class MarkBookByOrderCompletedUseCase @Inject constructor(
    @Named("BOOKS") private val repository: DailyPuzzleRepository
) {
    suspend operator fun invoke(date: String) {
        repository.markDailyCompleted(date)
    }
}