package com.example.mycomposeapp.core.domain.usecase.auth

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.core.domain.keys.PreferenceKeys
import com.example.mycomposeapp.core.domain.repository.AuthRepository
import com.example.mycomposeapp.core.domain.repository.DataStoreManager
import javax.inject.Inject

class GoogleSignInUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStoreManager: DataStoreManager
) {
    suspend operator fun invoke(idToken: String): Resource<String> {
        return when (val result = authRepository.signInWithGoogle(idToken)) {
            is Resource.Success -> {
                dataStoreManager.setPreference(PreferenceKeys.TOKEN, result.data)
                result
            }
            is Resource.Error -> result
            is Resource.Loading -> result
        }
    }
}
