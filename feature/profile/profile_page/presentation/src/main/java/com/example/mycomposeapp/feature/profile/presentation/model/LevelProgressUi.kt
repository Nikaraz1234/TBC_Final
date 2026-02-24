package com.example.mycomposeapp.feature.profile.presentation.model

data class LevelProgressUi(
    val level: Int,
    val xpIntoLevel: Int,
    val xpNeededForLevel: Int,
    val progress: Float
) {
    val xpText: String get() = "${formatNumber(xpIntoLevel)} / ${formatNumber(xpNeededForLevel)} XP"
    val levelText: String get() = "Level $level"
}

private fun formatNumber(n: Int): String = "%,d".format(n)
