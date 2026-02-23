package com.example.mycomposeapp.feature.game.domain.model

import com.example.mycomposeapp.feature.game.domain.model.books.BookCard
import com.example.mycomposeapp.feature.game.domain.model.books.SynopsisOption
import com.example.mycomposeapp.feature.game.domain.model.games.Achievement

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
    data class Screenshot(
        val imageUrl: String,
        val studio: String? = null,
        val genres: List<String> = emptyList(),
        val releaseYear: Int? = null
    ) : QuestionContent
    data class Description(
        val text: String,
        val studio: String? = null,
        val genres: List<String> = emptyList(),
        val releaseYear: Int? = null
    ) : QuestionContent
    data class Achievements(
        val achievements: List<Achievement>,
        val coverImageUrl: String = ""
    ) : QuestionContent

    data class Rankle(
        val id: Long,
        val imageUrl: String,
        val title: String,
        val rating: Double
    ) : QuestionContent

    data class BookSynopsis(
        val coverImageUrl: String,
        val options: List<SynopsisOption>
    ) : QuestionContent

    data class BookOddOneOut(
        val books: List<BookCard>,
        val traitDisplayName: String,
        val sharedValue: String
    ) : QuestionContent
}

data class PlotHint(
    val label: String,
    val value: String
)
