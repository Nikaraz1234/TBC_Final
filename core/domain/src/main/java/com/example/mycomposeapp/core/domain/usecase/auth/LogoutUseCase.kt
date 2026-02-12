package com.example.mycomposeapp.core.domain.usecase.auth

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.core.domain.keys.PreferenceKeys
import com.example.mycomposeapp.core.domain.repository.AuthRepository
import com.example.mycomposeapp.core.domain.usecase.datastore.RemovePreferenceUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val removePreferenceUseCase: RemovePreferenceUseCase
) {

    operator fun invoke(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)

        try {
            authRepository.logout()

            removePreferenceUseCase(
                listOf(PreferenceKeys.TOKEN)
            )

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Logout failed"))
        }
    }
}