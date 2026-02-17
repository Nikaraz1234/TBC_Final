package com.example.mycomposeapp.feature.game.domain.model

object GameConstants {

    const val XP_GAIN = 50
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
    const val PLOT_INITIAL_GUESSES = 3
    const val PLOT_HINT_COUNT = 6
    const val PLOT_HINT_COST = 20
    const val PLOT_BASE_POINTS = 100
    const val PLOT_STREAK_BONUS = 50
    const val PLOT_COINS_PER_CORRECT = 1

    // Achievement mode
    const val ACHIEVEMENT_INITIAL_LIVES = 3
    const val ACHIEVEMENT_INITIAL_VISIBLE = 3
    const val ACHIEVEMENT_REVEAL_STEP = 3
    const val ACHIEVEMENT_MAX_VISIBLE = 9
    const val ACHIEVEMENT_COINS_PER_CORRECT = 1
    const val ACHIEVEMENT_BASE_POINTS = 100
    const val ACHIEVEMENT_STREAK_BONUS = 50
    const val ACHIEVEMENT_HINT_COST = 20
    const val ACHIEVEMENT_BATCH_SIZE = 10
    const val ACHIEVEMENT_INITIAL_PAGE_LIMIT = 3
    const val ACHIEVEMENT_PAGE_EXPAND_STEP = 3

    // Guess the rating mode
    const val MANGA_RATING_PREFETCH_THRESHOLD = 2
    const val MANGA_RATING_COINS_PER_CORRECT = 1
    const val MANGA_RATING_BATCH_SIZE = 10

    // Rankle
    const val WIN_THRESHOLD = 0.05
    const val RANKLE_INITIAL_ATTEMPTS = 5
    const val RANKLE_COINS_WIN = 5

}
