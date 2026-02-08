package com.example.mycomposeapp.core.domain.repository

import com.example.mycomposeapp.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun refreshUser()
}
