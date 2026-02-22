package com.example.mycomposeapp.feature.profile.edit_profile.domain.usecase.user

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ChangeEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        currentPassword: String,
        newEmail: String
    ): Flow<Resource<Unit>> {

        if (newEmail.isBlank()) {
            return flowOf(Resource.Error("Email cannot be empty"))
        }

        if (!isValidEmail(newEmail)) {
            return flowOf(Resource.Error("Invalid email format"))
        }

        return authRepository.changeEmail(
            currentPassword = currentPassword,
            newEmail = newEmail
        )
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        return email.matches(emailRegex.toRegex())
    }
}
