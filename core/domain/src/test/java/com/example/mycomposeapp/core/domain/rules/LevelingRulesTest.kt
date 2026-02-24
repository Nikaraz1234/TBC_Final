package com.example.mycomposeapp.core.domain.rules

import org.junit.Test
import kotlin.test.assertEquals

class LevelingRulesTest {

    // ==================== xpToReachLevel ====================

    @Test
    fun `xpToReachLevel returns 0 for level 1`() {
        // Level 1 requires 0 XP (starting level)
        // Formula: 500 * (1-1)^2 = 500 * 0 = 0
        assertEquals(0, LevelingRules.xpToReachLevel(1))
    }

    @Test
    fun `xpToReachLevel returns 500 for level 2`() {
        // Formula: 500 * (2-1)^2 = 500 * 1 = 500
        assertEquals(500, LevelingRules.xpToReachLevel(2))
    }

    @Test
    fun `xpToReachLevel returns 2000 for level 3`() {
        // Formula: 500 * (3-1)^2 = 500 * 4 = 2000
        assertEquals(2000, LevelingRules.xpToReachLevel(3))
    }

    @Test
    fun `xpToReachLevel returns 4500 for level 4`() {
        // Formula: 500 * (4-1)^2 = 500 * 9 = 4500
        assertEquals(4500, LevelingRules.xpToReachLevel(4))
    }

    @Test
    fun `xpToReachLevel returns 8000 for level 5`() {
        // Formula: 500 * (5-1)^2 = 500 * 16 = 8000
        assertEquals(8000, LevelingRules.xpToReachLevel(5))
    }

    @Test
    fun `xpToReachLevel returns 40500 for level 10`() {
        // Formula: 500 * (10-1)^2 = 500 * 81 = 40500
        assertEquals(40500, LevelingRules.xpToReachLevel(10))
    }

    @Test
    fun `xpToReachLevel coerces level 0 to level 1 and returns 0`() {
        // Edge case: level 0 is coerced to 1
        assertEquals(0, LevelingRules.xpToReachLevel(0))
    }

    @Test
    fun `xpToReachLevel coerces negative level to level 1 and returns 0`() {
        // Edge case: negative levels are coerced to 1
        assertEquals(0, LevelingRules.xpToReachLevel(-5))
    }

    // ==================== calculateLevel ====================

    @Test
    fun `calculateLevel returns 1 for 0 XP`() {
        assertEquals(1, LevelingRules.calculateLevel(0))
    }

    @Test
    fun `calculateLevel returns 1 for XP just below level 2 threshold`() {
        // Level 2 requires 500 XP, so 499 should still be level 1
        assertEquals(1, LevelingRules.calculateLevel(499))
    }

    @Test
    fun `calculateLevel returns 2 for exactly 500 XP`() {
        // Level 2 threshold is exactly 500
        assertEquals(2, LevelingRules.calculateLevel(500))
    }

    @Test
    fun `calculateLevel returns 2 for XP between level 2 and 3`() {
        // Between 500 and 1999
        assertEquals(2, LevelingRules.calculateLevel(1000))
        assertEquals(2, LevelingRules.calculateLevel(1999))
    }

    @Test
    fun `calculateLevel returns 3 for exactly 2000 XP`() {
        // Level 3 threshold is 2000
        assertEquals(3, LevelingRules.calculateLevel(2000))
    }

    @Test
    fun `calculateLevel returns 4 for exactly 4500 XP`() {
        // Level 4 threshold is 4500
        assertEquals(4, LevelingRules.calculateLevel(4500))
    }

    @Test
    fun `calculateLevel returns 5 for exactly 8000 XP`() {
        // Level 5 threshold is 8000
        assertEquals(5, LevelingRules.calculateLevel(8000))
    }

    @Test
    fun `calculateLevel returns 10 for exactly 40500 XP`() {
        // Level 10 threshold is 40500
        assertEquals(10, LevelingRules.calculateLevel(40500))
    }

    @Test
    fun `calculateLevel handles large XP values`() {
        // Level 100: 500 * 99^2 = 4,900,500
        assertEquals(100, LevelingRules.calculateLevel(4_900_500))
        // Just above level 100
        assertEquals(100, LevelingRules.calculateLevel(4_900_501))
    }

    @Test
    fun `calculateLevel coerces negative XP to 0 and returns level 1`() {
        // Edge case: negative XP is coerced to 0
        assertEquals(1, LevelingRules.calculateLevel(-100))
        assertEquals(1, LevelingRules.calculateLevel(-1))
    }

    // ==================== Consistency Check ====================

    @Test
    fun `calculateLevel and xpToReachLevel are consistent`() {
        // For any level, calculateLevel(xpToReachLevel(level)) should return that level
        for (level in 1..20) {
            val xpNeeded = LevelingRules.xpToReachLevel(level)
            assertEquals(level, LevelingRules.calculateLevel(xpNeeded))
        }
    }

    @Test
    fun `calculateLevel returns previous level when 1 XP below threshold`() {
        // For levels 2-10, verify XP just below threshold gives previous level
        for (level in 2..10) {
            val xpNeeded = LevelingRules.xpToReachLevel(level)
            assertEquals(level - 1, LevelingRules.calculateLevel(xpNeeded - 1))
        }
    }
}