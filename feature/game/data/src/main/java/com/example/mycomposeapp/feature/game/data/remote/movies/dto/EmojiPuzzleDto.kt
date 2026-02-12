package com.example.mycomposeapp.feature.game.data.remote.movies.dto

data class EmojiPuzzleDto(
    val tmdbMovieId: Int = 0,
    val movieTitle: String = "",
    val emojis: String = "",
    val mainActor: String = "",
    val date: String = ""
)
