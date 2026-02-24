package com.example.mycomposeapp.feature.game.domain.constants

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

    // Search
    const val MIN_SEARCH_QUERY_LENGTH = 2
    const val SEARCH_DEBOUNCE_MS = 300L

    // Cover mode (batch/prefetch)
    const val COVER_BATCH_SIZE = 5
    const val COVER_PREFETCH_THRESHOLD = 2
    const val COVER_PAGE_WINDOW = 10

    // Description mode
    const val DESCRIPTION_BATCH_SIZE = 10
    const val DESCRIPTION_PREFETCH_THRESHOLD = 2
    const val DESCRIPTION_MAX_HINT_STEPS = 3

    // Achievement mode (prefetch)
    const val ACHIEVEMENT_PREFETCH_THRESHOLD = 2

    // Guess the rating mode
    const val MANGA_RATING_PREFETCH_THRESHOLD = 2
    const val MANGA_RATING_COINS_PER_CORRECT = 1
    const val MANGA_RATING_BATCH_SIZE = 10

    // Rankle
    const val WIN_THRESHOLD = 0.01
    const val RANKLE_INITIAL_ATTEMPTS = 5
    const val RANKLE_INITIAL_LIVES = 1
    const val RANKLE_COINS_WIN = 5

    // Book Synopsis mode
    const val BOOK_SYNOPSIS_BATCH_SIZE = 5
    const val BOOK_SYNOPSIS_PREFETCH_THRESHOLD = 2
    const val BOOK_SYNOPSIS_START_INDEX_MAX_INIT = 10
    const val BOOK_SYNOPSIS_START_INDEX_EXPAND_STEP = 5
    const val BOOK_SYNOPSIS_START_INDEX_EXPAND_AMOUNT = 20
    const val BOOK_SYNOPSIS_HINT_COST = 20
    const val BOOK_SYNOPSIS_COINS_PER_CORRECT = 1
    const val BOOK_SYNOPSIS_BASE_POINTS = 100
    const val BOOK_SYNOPSIS_STREAK_BONUS = 50

    // Book Odd One Out mode
    const val BOOK_ODD_ONE_OUT_POOL_SIZE = 60
    const val BOOK_ODD_ONE_OUT_BATCH_SIZE = 3
    const val BOOK_ODD_ONE_OUT_PREFETCH_THRESHOLD = 1
    const val BOOK_ODD_ONE_OUT_START_INDEX_MAX_INIT = 10
    const val BOOK_ODD_ONE_OUT_START_INDEX_EXPAND_STEP = 5
    const val BOOK_ODD_ONE_OUT_START_INDEX_EXPAND_AMOUNT = 20
    const val BOOK_ODD_ONE_OUT_HINT_COST = 20
    const val BOOK_ODD_ONE_OUT_CATEGORY_HINT_COST = 15
    const val BOOK_ODD_ONE_OUT_COINS_PER_CORRECT = 1
    const val BOOK_ODD_ONE_OUT_BASE_POINTS = 100
    const val BOOK_ODD_ONE_OUT_STREAK_BONUS = 50

}
