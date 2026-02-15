package com.example.mycomposeapp.feature.game.domain.model

import com.example.mycomposeapp.feature.game.domain.model.game.Achievement

data class Question(
    val id: String,
    val correctAnswer: String,
    val content: QuestionContent
)

sealed interface QuestionContent {
    data class Cover(val imageUrl: String) : QuestionContent
    data class Emoji(val emojiClues: String, val isDaily: Boolean, val date: String, val hintText: String = "") : QuestionContent
    data class Plot(
        val plotSummary: String,
        val imageUrl: String = "",
        val hints: List<PlotHint> = emptyList()
    ) : QuestionContent
    data class Screenshot(val imageUrl: String) : QuestionContent
    data class Achievements(
        val achievements: List<Achievement>,
        val coverImageUrl: String = ""
    ) : QuestionContent
}

data class PlotHint(
    val label: String,
    val value: String
)
