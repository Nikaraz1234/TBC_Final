package com.example.mycomposeapp.core.domain.rules

import com.example.mycomposeapp.core.domain.model.LevelProgress

object LevelingRules {
    fun xpToReachLevel(level: Int): Int {
        val l = level.coerceAtLeast(1)
        val n = l - 1
        return 500 * n * n
    }

    fun calculateLevel(totalXp: Int): Int {
        val xp = totalXp.coerceAtLeast(0)
        var level = 1
        while (xp >= xpToReachLevel(level + 1)) level++
        return level
    }
}
