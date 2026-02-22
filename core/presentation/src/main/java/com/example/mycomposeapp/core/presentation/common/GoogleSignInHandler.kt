package com.example.mycomposeapp.core.presentation.common

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.core.domain.usecase.auth.GoogleSignInUseCase
import javax.inject.Inject

class GoogleSignInHandler @Inject constructor(
    private val googleSignInUseCase: GoogleSignInUseCase
) {
    suspend fun handle(idToken: String): Resource<String> {
        return googleSignInUseCase(idToken)
    }
}
