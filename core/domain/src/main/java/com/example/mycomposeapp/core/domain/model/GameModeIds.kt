package com.example.mycomposeapp.core.domain.model

object GameModeIds {
    const val COVER = "cover"
    const val EMOJI = "emoji"
    const val PLOT = "plot"

    const val GAME_SCREENSHOT = "games_screenshot"
    const val GAME_DESCRIPTION = "games_description"

    fun statsKey(categoryType: String, gameModeId: String): String =
        "${categoryType}_${gameModeId}"
}
