package com.example.mycomposeapp.feature.game.domain.repository

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.feature.game.domain.model.DailyPuzzle
import kotlinx.coroutines.flow.Flow

interface DailyPuzzleRepository {
    fun getDailyPuzzle(date: String): Flow<Resource<DailyPuzzle>>
    fun getArchivePuzzles(): Flow<Resource<List<DailyPuzzle>>>
    suspend fun markDailyCompleted(date: String)
    suspend fun seedPuzzles() {}
}
