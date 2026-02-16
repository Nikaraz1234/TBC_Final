package com.example.mycomposeapp.feature.game.domain.model
enum class FeedbackColor { GREEN, RED, ORANGE, YELLOW, WHITE, GRAY }
enum class ArrowDirection { UP, DOWN, NONE }

data class RankleGuess(
    val input: Double,
    val color: FeedbackColor,
    val direction: ArrowDirection
)
