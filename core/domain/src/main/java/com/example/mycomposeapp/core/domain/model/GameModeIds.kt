package com.example.mycomposeapp.core.domain.model

object GameModeIds {
    const val COVER = "cover"
    const val EMOJI = "emoji"
    const val PLOT = "plot"

    const val GAME_SCREENSHOT = "games_screenshot"
    const val GAME_DESCRIPTION = "games_description"
    const val GAME_ACHIEVEMENT = "games_achievement"

    const val MANGA_RATING = "manga_rating"

    const val RANKLE = "rankle"

    const val BOOK_SYNOPSIS = "book_synopsis"
    const val BOOK_ODD_ONE_OUT = "book_odd_one_out"

    fun statsKey(categoryType: String, gameModeId: String): String =
        "${categoryType}_${gameModeId}"
}
