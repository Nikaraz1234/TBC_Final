package com.example.mycomposeapp.domain.usecase.auth

import com.example.mycomposeapp.domain.model.AuthResult
import com.example.mycomposeapp.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        displayName: String
    ): AuthResult {
        return authRepository.register(email, password, displayName)
    }
}
