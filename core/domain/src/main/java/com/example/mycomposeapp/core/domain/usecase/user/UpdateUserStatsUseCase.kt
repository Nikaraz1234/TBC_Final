package com.example.mycomposeapp.core.domain.usecase.user

import com.example.mycomposeapp.core.domain.model.UserStats
import com.example.mycomposeapp.core.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserStatsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(stats: UserStats) {
        userRepository.refreshUser()

        userRepository.updateUserStats(stats)
    }
}
