package com.example.mycomposeapp.core.domain.usecase.user

import com.example.mycomposeapp.core.domain.repository.UserRepository
import javax.inject.Inject

class UpdateCoinsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(coins: Int) {
        userRepository.updateCoins(coins)
    }
}
