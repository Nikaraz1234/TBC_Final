package com.example.mycomposeapp.feature.achievements.data.mapper

import com.example.mycomposeapp.feature.achievements.data.dto.AchievementDto
import com.example.mycomposeapp.feature.achievements.domain.model.AchievementCategory
import com.example.mycomposeapp.feature.achievements.domain.model.AchievementConditionType
import com.example.mycomposeapp.feature.achievements.domain.model.AppAchievement

fun AchievementDto.toDomain(): AppAchievement = AppAchievement(
    id = id,
    name = name,
    description = description,
    icon = icon,
    category = runCatching { AchievementCategory.valueOf(category) }
        .getOrDefault(AchievementCategory.GENERAL),
    xpReward = xpReward,
    conditionType = runCatching { AchievementConditionType.valueOf(conditionType) }
        .getOrDefault(AchievementConditionType.GAMES_PLAYED),
    conditionValue = conditionValue,
    conditionKey = conditionKey
)
