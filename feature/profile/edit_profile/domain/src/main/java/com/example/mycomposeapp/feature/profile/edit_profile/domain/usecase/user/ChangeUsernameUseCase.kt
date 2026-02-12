package com.example.mycomposeapp.feature.profile.edit_profile.domain.usecase.user

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.core.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ChangeUsernameUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(newUsername: String): Flow<Resource<Unit>> {

        val trimmed = newUsername.trim()

        if (trimmed.isBlank()) {
            return flowOf(Resource.Error("Username cannot be empty"))
        }

        if (trimmed.length < 3) {
            return flowOf(Resource.Error("Username must be at least 3 characters"))
        }

        if (trimmed.length > 20) {
            return flowOf(Resource.Error("Username must be at most 20 characters"))
        }

        val regex = "^[A-Za-z0-9._]+$".toRegex()
        if (!regex.matches(trimmed)) {
            return flowOf(Resource.Error("Username contains invalid characters"))
        }

        return userRepository.changeUsername(trimmed)
    }
}

