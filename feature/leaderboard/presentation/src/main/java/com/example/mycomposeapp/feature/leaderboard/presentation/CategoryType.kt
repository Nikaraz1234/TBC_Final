package com.example.mycomposeapp.feature.leaderboard.presentation

enum class CategoryType(
    val label: String,
    val firestorePrefix: String
) {
    Movie("Movie", "MOVIES"),
    Game("Game", "GAMES"),
    Comics("Comics", "COMICS")
}