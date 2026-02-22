package com.example.mycomposeapp.feature.achievements.domain.usecase

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.feature.achievements.domain.model.AppAchievement
import com.example.mycomposeapp.feature.achievements.domain.repository.AchievementRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAchievementsUseCase @Inject constructor(
    private val repository: AchievementRepository
) {
    operator fun invoke(): Flow<Resource<List<AppAchievement>>> = repository.getAchievements()
}
