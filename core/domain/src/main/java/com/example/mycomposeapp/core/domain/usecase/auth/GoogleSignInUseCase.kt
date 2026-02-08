package com.example.mycomposeapp.core.domain.usecase.auth

import com.example.mycomposeapp.core.domain.model.AuthResult
import com.example.mycomposeapp.core.domain.repository.AuthRepository
import javax.inject.Inject

class GoogleSignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): AuthResult {
        return authRepository.signInWithGoogle(idToken)
    }
}
