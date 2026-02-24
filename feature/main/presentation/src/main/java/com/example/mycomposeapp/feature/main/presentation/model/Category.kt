package com.example.mycomposeapp.feature.main.presentation.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.mycomposeapp.core.domain.model.CategoryType
import com.example.mycomposeapp.core.domain.model.GameModeIds
import com.example.mycomposeapp.core.ui.R as CoreUiR
import com.example.mycomposeapp.feature.main.presentation.R

data class Category(
    val type: CategoryType,
    val name: String,
    val gradientColors: List<Color>,
    val gameModes: List<GameMode>,
    @DrawableRes val backgroundImageRes: Int? = null
)

object Categories {
    private val movieGameModes = listOf(
        GameMode(
            id = GameModeIds.COVER,
            name = "By Cover",
            description = "Guess the movie from its poster",
            iconRes = R.drawable.ic_cover,
            isAvailable = true
        ),
        GameMode(
            id = GameModeIds.EMOJI,
            name = "By Emoji",
            description = "Decode emoji clues to find the movie",
            iconRes = R.drawable.ic_emoji,
            isAvailable = true
        ),
        GameMode(
            id = GameModeIds.PLOT,
            name = "By Plot Summary",
            description = "Identify the movie from its plot",
            iconRes = R.drawable.ic_plot,
            isAvailable = true
        )
    )

    private val gamesCategories = listOf(
        GameMode(
            id = GameModeIds.GAME_SCREENSHOT,
            name = "By Screenshot",
            description = "Identify game from screenshot",
            iconRes = CoreUiR.drawable.ic_games_by_screenshot,
            isAvailable = true
        ),
        GameMode(
            id = GameModeIds.GAME_ACHIEVEMENT,
            name = "By Achievements",
            description = "Guess the game from its achievements",
            iconRes = R.drawable.ic_achievement,
            isAvailable = true
        ),
        GameMode(
            id = GameModeIds.GAME_DESCRIPTION,
            name = "By Description",
            description = "Identify game from description",
            iconRes = CoreUiR.drawable.ic_games_by_description,
            isAvailable = true
        )

    )

    private val comicsGameModes = listOf(
        GameMode(
            id = GameModeIds.MANGA_RATING,
            name = "Compare the Rating",
            description = "Guess which manga is rated higher",
            iconRes = CoreUiR.drawable.ic_comics_compare_the_rating,
            isAvailable = true
        ),
        GameMode(
            id = GameModeIds.EMOJI,
            name = "By Emoji",
            description = "Decode emoji clues to find the manga",
            iconRes = R.drawable.ic_emoji,
            isAvailable = true
        ),
        GameMode(
            id = GameModeIds.RANKLE,
            name = "Guess the rating",
            description = "Guess the close approximation of the rating",
            iconRes = CoreUiR.drawable.ic_comics_guess_the_rating,
            isAvailable = true
        )
    )


    private val booksGameModes = listOf(
        GameMode(
            id = GameModeIds.BOOK_SYNOPSIS,
            name = "Guess the Synopsis",
            description = "Which synopsis belongs to this book cover?",
            iconRes = CoreUiR.drawable.ic_books_guess_the_synopsis,
            isAvailable = true
        ),
        GameMode(
            id = GameModeIds.BOOK_ODD_ONE_OUT,
            name = "Odd One Out",
            description = "Find the book that doesn't fit with the others",
            iconRes = CoreUiR.drawable.ic_books_odd_one_out,
            isAvailable = true
        ),
        GameMode(
                id = GameModeIds.BOOK_BY_ORDER,
        name = "Order Book Events",
        description = "Order Book Events",
        iconRes = R.drawable.ic_cover,
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
            gameModes = movieGameModes,
            backgroundImageRes = CoreUiR.drawable.movie_cover
        ),
        Category(
            type = CategoryType.GAMES,
            name = "Games",
            gradientColors = listOf(Color(0xFF4CAF50), Color(0xFF2196F3)),
            gameModes = gamesCategories,
            backgroundImageRes = CoreUiR.drawable.games_cover

        ),
        Category(
            type = CategoryType.COMICS,
            name = "Comics",
            gradientColors = listOf(Color(0xFFFF9800), Color(0xFFFF5722)),
            gameModes = comicsGameModes,
            backgroundImageRes = CoreUiR.drawable.comics_cover
        ),
        Category(
            type = CategoryType.BOOKS,
            name = "Books & Novels",
            gradientColors = listOf(Color(0xFF795548), Color(0xFF607D8B)),
            gameModes = booksGameModes,
            backgroundImageRes = CoreUiR.drawable.books_cover
        ),
        Category(
            type = CategoryType.SPORTS,
            name = "Sports",
            gradientColors = listOf(Color(0xFF00BCD4), Color(0xFF3F51B5)),
            gameModes = comingSoonGameModes,
            backgroundImageRes = CoreUiR.drawable.sports_cover
        )
    )
}
