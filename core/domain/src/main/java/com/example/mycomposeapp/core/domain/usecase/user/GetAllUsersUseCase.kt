package com.example.mycomposeapp.core.domain.usecase.user

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.core.domain.model.User
import com.example.mycomposeapp.core.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<Resource<List<User>>> {
        return userRepository.getAllUser()
    }
}