package com.example.mycomposeapp.feature.profile.edit_profile.domain.usecase

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.feature.profile.edit_profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdatePhotoUrlUseCase @Inject constructor(
    private val repo: ProfileRepository
) {
    operator fun invoke(userId: String, photoUrl: String): Flow<Resource<Unit>> {
        return repo.updatePhotoUrl(userId, photoUrl)
    }
}