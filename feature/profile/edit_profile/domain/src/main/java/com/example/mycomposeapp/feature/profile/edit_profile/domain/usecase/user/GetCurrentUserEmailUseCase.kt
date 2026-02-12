package com.example.mycomposeapp.feature.profile.edit_profile.domain.usecase.user

import com.example.mycomposeapp.core.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): String? = authRepository.getCurrentUserEmail()
}