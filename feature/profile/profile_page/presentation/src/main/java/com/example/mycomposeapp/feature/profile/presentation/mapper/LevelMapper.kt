package com.example.mycomposeapp.feature.profile.presentation.mapper

import com.example.mycomposeapp.core.domain.LevelingRules
import com.example.mycomposeapp.feature.profile.presentation.model.LevelProgressUi

fun toLevelProgressUi(totalXp: Int, level: Int): LevelProgressUi {
    val lvl = level.coerceAtLeast(1)
    val startXp = LevelingRules.xpToReachLevel(lvl)
    val nextXp = LevelingRules.xpToReachLevel(lvl + 1)

    val needed = (nextXp - startXp).coerceAtLeast(1)
    val into = (totalXp - startXp).coerceIn(0, needed)
    val progress = (into.toFloat() / needed.toFloat()).coerceIn(0f, 1f)

    return LevelProgressUi(
        level = lvl,
        xpIntoLevel = into,
        xpNeededForLevel = needed,
        progress = progress
    )
}