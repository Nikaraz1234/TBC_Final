package com.example.mycomposeapp.core.domain.usecase.user

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.core.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<Resource<Unit>>{
        return userRepository.deleteCurrentUser()
    }
}