package com.example.mycomposeapp.feature.game.domain.usecase.books

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.feature.game.domain.model.DailyPuzzle
import com.example.mycomposeapp.feature.game.domain.repository.DailyPuzzleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Named

class GetBookByOrderArchiveUseCase @Inject constructor(
    @Named("BOOKS") private val repository: DailyPuzzleRepository
) {
    operator fun invoke(): Flow<Resource<List<DailyPuzzle>>> {
        return repository.getArchivePuzzles()
    }
}