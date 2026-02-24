package com.example.mycomposeapp.feature.profile.edit_profile.domain.repository

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun uploadProfilePhoto(userId: String, uriString: String): Flow<Resource<Unit>>
    fun updatePhotoUrl(userId: String, photoUrl: String): Flow<Resource<Unit>>
    fun observeUser(userId: String): Flow<Resource<User>>

}