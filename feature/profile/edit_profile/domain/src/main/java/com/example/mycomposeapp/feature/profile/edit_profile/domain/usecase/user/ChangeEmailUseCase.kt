package com.example.mycomposeapp.feature.profile.edit_profile.domain.usecase.user

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.core.domain.repository.AuthRepository
import com.example.mycomposeapp.core.domain.usecase.validation.ValidateEmailUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ChangeEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val validateEmail: ValidateEmailUseCase
) {
    suspend operator fun invoke(
        currentPassword: String,
        newEmail: String
    ): Flow<Resource<Unit>> {
        val validation = validateEmail(newEmail)
        if (!validation.isValid) {
            return flowOf(Resource.Error(validation.errorMessage ?: "Invalid email"))
        }

        return authRepository.changeEmail(
            currentPassword = currentPassword,
            newEmail = newEmail
        )
    }
}
