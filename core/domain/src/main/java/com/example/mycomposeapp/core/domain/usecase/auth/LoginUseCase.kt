package com.example.mycomposeapp.core.domain.usecase.auth

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.core.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Resource<String> {
        return authRepository.login(email, password)
    }
}
