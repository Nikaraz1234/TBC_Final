package com.example.mycomposeapp.feature.achievements.domain.model

enum class AchievementCategory {
    GENERAL,
    MOVIES,
    GAMES,
    COMICS,
    BOOKS,
    SPORTS;

    fun displayName(): String = when (this) {
        GENERAL -> "General"
        MOVIES -> "Movies"
        GAMES -> "Games"
        COMICS -> "Comics"
        BOOKS -> "Books & Novels"
        SPORTS -> "Sports"
    }
}
