package com.example.mycomposeapp.feature.profile.edit_profile.domain.usecase.user

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    operator fun invoke(
        currentPassword: String,
        newPassword: String
    ): Flow<Resource<Unit>> {

        if (newPassword.length < 6) {
            return flowOf(Resource.Error("Password must be at least 6 characters"))
        }

        return authRepository.changePassword(currentPassword, newPassword)
    }
}
