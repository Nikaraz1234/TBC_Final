package com.example.mycomposeapp.feature.game.data.mapper.books

import com.example.mycomposeapp.feature.game.data.remote.books.dto.GoogleBooksVolumeDto
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent
import com.example.mycomposeapp.feature.game.domain.model.books.SynopsisOption

fun buildSynopsisQuestion(
    target: GoogleBooksVolumeDto,
    fakeOptions: List<GoogleBooksVolumeDto>
): Question? {
    val coverUrl = target.volumeInfo.imageLinks?.thumbnail?.toHttps() ?: return null
    val synopsis = target.volumeInfo.description ?: return null
    if (synopsis.isBlank()) return null

    val correctOption = SynopsisOption(
        bookId = target.id,
        bookTitle = target.volumeInfo.title,
        synopsis = synopsis
    )

    val fakeOptionsMapped = fakeOptions.mapNotNull { dto ->
        val fakeSynopsis = dto.volumeInfo.description ?: return@mapNotNull null
        if (fakeSynopsis.isBlank()) return@mapNotNull null
        SynopsisOption(
            bookId = dto.id,
            bookTitle = dto.volumeInfo.title,
            synopsis = fakeSynopsis
        )
    }.take(3)

    if (fakeOptionsMapped.size < 3) return null

    val allOptions = (listOf(correctOption) + fakeOptionsMapped).shuffled()

    return Question(
        id = "book_synopsis_${target.id}",
        correctAnswer = target.volumeInfo.title,
        content = QuestionContent.BookSynopsis(
            coverImageUrl = coverUrl,
            options = allOptions
        )
    )
}

private fun String.toHttps(): String = replace("http://", "https://")
