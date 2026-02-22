package com.example.mycomposeapp.core.domain.repository

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.core.domain.model.User
import com.example.mycomposeapp.core.domain.model.UserStats
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun refreshUser()
    suspend fun updateUserStats(stats: UserStats)
    suspend fun updateCoins(coins: Int)
    fun changeUsername(newUsername: String): Flow<Resource<Unit>>
    fun getAllUser(): Flow<Resource<List<User>>>
    fun deleteCurrentUser(): Flow<Resource<Unit>>

}