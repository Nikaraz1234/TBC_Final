package com.example.mycomposeapp.core.domain.repository

import com.example.mycomposeapp.core.domain.Resource

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<String>
    suspend fun register(email: String, password: String, displayName: String): Resource<String>
    suspend fun signInWithGoogle(idToken: String): Resource<String>
    suspend fun isUserLoggedIn(): Boolean
    suspend fun logout()
    fun getCurrentUserId(): String?
}
