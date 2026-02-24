package com.example.mycomposeapp.feature.game.data.mapper.games

import com.example.mycomposeapp.feature.game.data.remote.games.dto.SteamAchievementDto
import com.example.mycomposeapp.feature.game.data.remote.games.dto.SteamAchievementPercentageDto
import com.example.mycomposeapp.feature.game.domain.model.games.Achievement

fun SteamAchievementDto.toAchievement(): Achievement = Achievement(
    name = displayName ?: name,
    description = description ?: "",
    iconUrl = icon ?: icongray ?: ""
)

fun mergePercentages(
    achievements: List<Achievement>,
    dtoAchievements: List<SteamAchievementDto>,
    percentages: List<SteamAchievementPercentageDto>
): List<Achievement> {
    val percentageMap = percentages.associateBy { it.name }
    return achievements.mapIndexed { index, achievement ->
        val internalName = dtoAchievements.getOrNull(index)?.name ?: ""
        val pct = percentageMap[internalName]?.percent ?: 0f
        achievement.copy(unlockPercentage = pct)
    }
}

fun steamCoverUrl(appId: Int): String =
    "https://cdn.cloudflare.steamstatic.com/steam/apps/$appId/library_600x900.jpg"
