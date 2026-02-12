package com.example.mycomposeapp.feature.profile.edit_profile.domain.usecase

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.core.domain.model.User
import com.example.mycomposeapp.feature.profile.edit_profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserUseCase @Inject constructor(
    private val repo: ProfileRepository
) {
    operator fun invoke(userId: String): Flow<Resource<User>> {
        return repo.observeUser(userId)
    }
}