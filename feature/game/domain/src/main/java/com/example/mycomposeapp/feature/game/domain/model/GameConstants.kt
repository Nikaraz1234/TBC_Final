package com.example.mycomposeapp.feature.game.domain.model

object GameConstants {
    // Timer
    const val TIME_LIMIT_SECONDS = 30

    // Cover mode
    const val COVER_INITIAL_LIVES = 3
    const val COVER_INITIAL_REVEAL_COST = 10
    const val COVER_GRID_CELLS = 9
    const val COVER_COINS_PER_CORRECT = 1
    const val COVER_BASE_POINTS = 100
    const val COVER_STREAK_BONUS = 50

    // Scoring (shared)
    const val BASE_POINTS = 100
    const val STREAK_BONUS_MULTIPLIER = 50
    const val TIME_BONUS_MAX = 50

    // Emoji mode
    const val EMOJI_INITIAL_GUESSES = 3
    const val EMOJI_HINT_COST = 20
    const val EMOJI_DAILY_COINS_REWARD = 15

    // Plot mode
    const val PLOT_QUESTION_COUNT = 10
    const val PLOT_INITIAL_BLUR = 25f
    const val PLOT_BLUR_STEP = 5f
}
