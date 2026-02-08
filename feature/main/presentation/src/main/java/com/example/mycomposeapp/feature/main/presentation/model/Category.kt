package com.example.mycomposeapp.feature.main.presentation.model

import androidx.compose.ui.graphics.Color
import com.example.mycomposeapp.feature.main.presentation.R

enum class CategoryType {
    MOVIES,
    GAMES,
    COMICS,
    BOOKS,
    SPORTS
}

data class Category(
    val type: CategoryType,
    val name: String,
    val gradientColors: List<Color>,
    val gameModes: List<GameMode>
)

object Categories {
    private val movieGameModes = listOf(
        GameMode(
            id = "movies_cover",
            name = "By Cover",
            description = "Guess the movie from its poster",
            iconRes = R.drawable.ic_cover,
            isAvailable = true
        ),
        GameMode(
            id = "movies_emoji",
            name = "By Emoji",
            description = "Decode emoji clues to find the movie",
            iconRes = R.drawable.ic_emoji,
            isAvailable = true
        ),
        GameMode(
            id = "movies_plot",
            name = "By Plot Summary",
            description = "Identify the movie from its plot",
            iconRes = R.drawable.ic_plot,
            isAvailable = true
        )
    )

    private val comingSoonGameModes = listOf(
        GameMode(
            id = "coming_soon",
            name = "Coming Soon",
            description = "New game modes are on the way!",
            iconRes = R.drawable.ic_cover,
            isAvailable = false
        )
    )

    val all = listOf(
        Category(
            type = CategoryType.MOVIES,
            name = "Movies",
            gradientColors = listOf(Color(0xFFE91E63), Color(0xFF9C27B0)),
            gameModes = movieGameModes
        ),
        Category(
            type = CategoryType.GAMES,
            name = "Games",
            gradientColors = listOf(Color(0xFF4CAF50), Color(0xFF2196F3)),
            gameModes = comingSoonGameModes
        ),
        Category(
            type = CategoryType.COMICS,
            name = "Comics",
            gradientColors = listOf(Color(0xFFFF9800), Color(0xFFFF5722)),
            gameModes = comingSoonGameModes
        ),
        Category(
            type = CategoryType.BOOKS,
            name = "Books & Novels",
            gradientColors = listOf(Color(0xFF795548), Color(0xFF607D8B)),
            gameModes = comingSoonGameModes
        ),
        Category(
            type = CategoryType.SPORTS,
            name = "Sports",
            gradientColors = listOf(Color(0xFF00BCD4), Color(0xFF3F51B5)),
            gameModes = comingSoonGameModes
        )
    )
}
