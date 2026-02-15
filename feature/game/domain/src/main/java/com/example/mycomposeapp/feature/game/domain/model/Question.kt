package com.example.mycomposeapp.feature.game.domain.model

    data class Question(
        val id: String,
        val correctAnswer: String,
        val content: QuestionContent
    )

sealed interface QuestionContent {
    data class Cover(val imageUrl: String) : QuestionContent
    data class Emoji(val emojiClues: String, val isDaily: Boolean, val date: String, val hintText: String = "") : QuestionContent
    data class Plot(val plotSummary: String) : QuestionContent

    data class Screenshot(
        val imageUrl: String,
        val studio: String? = null,
        val genres: List<String> = emptyList(),
        val releaseYear: Int? = null
    ) : QuestionContent

    data class Description(
        val text: String,
        val studio: String?,
        val genres: List<String>,
        val releaseYear: Int?
    ) : QuestionContent
}
