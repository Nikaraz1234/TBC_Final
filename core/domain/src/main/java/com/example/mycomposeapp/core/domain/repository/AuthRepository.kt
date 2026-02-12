package com.example.mycomposeapp.core.domain.repository

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.core.domain.model.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResult
    suspend fun register(email: String, password: String, displayName: String): AuthResult
    suspend fun signInWithGoogle(idToken: String): AuthResult
    suspend fun isUserLoggedIn(): Boolean
    suspend fun logout()
    fun getCurrentUserId(): String?
    fun getCurrentUserEmail(): String?
    fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Flow<Resource<Unit>>

    fun changeEmail(
        currentPassword: String,
        newEmail: String
    ): Flow<Resource<Unit>>


}
