package com.example.mycomposeapp.core.domain.model

object GameModeIds {
    const val COVER = "cover"
    const val EMOJI = "emoji"
    const val PLOT = "plot"

    fun statsKey(categoryType: String, gameModeId: String): String =
        "${categoryType}_${gameModeId}"
}
