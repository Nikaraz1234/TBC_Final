package com.example.mycomposeapp.feature.profile.edit_profile.domain.usecase.user

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.core.domain.repository.AuthRepository
import com.example.mycomposeapp.core.domain.usecase.validation.ValidatePasswordUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val validatePassword: ValidatePasswordUseCase
) {
    operator fun invoke(
        currentPassword: String,
        newPassword: String
    ): Flow<Resource<Unit>> {
        val validation = validatePassword(newPassword)
        if (!validation.isValid) {
            return flowOf(Resource.Error(validation.errorMessage ?: "Invalid password"))
        }

        return authRepository.changePassword(currentPassword, newPassword)
    }
}
