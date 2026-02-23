package com.example.mycomposeapp.feature.game.data.mapper.books

import com.example.mycomposeapp.feature.game.data.remote.books.dto.GoogleBooksVolumeDto
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent
import com.example.mycomposeapp.feature.game.domain.model.books.BookCard

private val COLORS = listOf(
    "red", "blue", "green", "black", "white", "yellow", "orange", "purple",
    "gold", "silver", "grey", "gray", "pink", "crimson", "scarlet", "amber"
)

private val ANIMALS = listOf(
    "fox", "bear", "wolf", "lion", "dog", "cat", "horse", "bird", "crow",
    "rabbit", "snake", "dragon", "fish", "deer", "owl", "whale", "shark",
    "tiger", "eagle", "hawk", "raven", "spider", "bee", "ant", "rat",
    "pig", "duck", "frog", "mouse", "hare"
)

enum class BookTrait {
    SAME_AUTHOR, SAME_GENRE, SAME_PAGE_COUNT, SAME_WORD_COUNT,
    STARTS_WITH_THE, QUESTION_TITLE, COLOR_IN_TITLE, ANIMAL_IN_TITLE,
    SINGLE_WORD_TITLE, SAME_PUBLISHED_YEAR, SAME_PUBLISHER
}

data class TraitGroup(
    val traitDisplayName: String,
    val sharedValue: String,
    val matching: List<GoogleBooksVolumeDto>
)

fun findTraitGroups(pool: List<GoogleBooksVolumeDto>): List<Pair<BookTrait, TraitGroup>> {
    val results = mutableListOf<Pair<BookTrait, TraitGroup>>()
    BookTrait.entries.forEach { trait ->
        val groups = groupByTrait(pool, trait)
        groups.forEach { group ->
            if (group.matching.size >= 3) {
                results.add(trait to group)
            }
        }
    }
    return results
}

private fun groupByTrait(pool: List<GoogleBooksVolumeDto>, trait: BookTrait): List<TraitGroup> {
    return when (trait) {
        BookTrait.SAME_AUTHOR -> {
            pool.groupBy { it.volumeInfo.authors.firstOrNull() ?: "" }
                .filter { it.key.isNotBlank() }
                .map { (author, books) -> TraitGroup("Same Author", author, books) }
        }
        BookTrait.SAME_GENRE -> {
            pool.groupBy { it.volumeInfo.categories.firstOrNull()?.normalizeGenre() ?: "" }
                .filter { it.key.isNotBlank() }
                .map { (genre, books) -> TraitGroup("Same Genre", genre, books) }
        }
        BookTrait.SAME_PAGE_COUNT -> {
            val buckets = mutableMapOf<Int, MutableList<GoogleBooksVolumeDto>>()
            pool.forEach { dto ->
                val pages = dto.volumeInfo.pageCount ?: return@forEach
                if (pages <= 0) return@forEach
                val bucket = (pages / 20) * 20
                buckets.getOrPut(bucket) { mutableListOf() }.add(dto)
            }
            buckets.filter { it.value.size >= 3 }
                .map { (bucket, books) -> TraitGroup("Same Page Count", "~${bucket + 10} pages", books) }
        }
        BookTrait.SAME_WORD_COUNT -> {
            pool.groupBy { it.volumeInfo.title.split(" ").size }
                .filter { it.key >= 2 }
                .map { (count, books) -> TraitGroup("Same Word Count in Title", "$count words", books) }
        }
        BookTrait.STARTS_WITH_THE -> {
            val matching = pool.filter { it.volumeInfo.title.startsWith("The ", ignoreCase = true) }
            if (matching.size >= 3) listOf(TraitGroup("Starts with 'The'", "Starts with 'The'", matching))
            else emptyList()
        }
        BookTrait.QUESTION_TITLE -> {
            val matching = pool.filter { it.volumeInfo.title.endsWith("?") }
            if (matching.size >= 3) listOf(TraitGroup("Title is a Question", "Title ends with '?'", matching))
            else emptyList()
        }
        BookTrait.COLOR_IN_TITLE -> {
            val matching = pool.filter { dto ->
                val lower = dto.volumeInfo.title.lowercase()
                COLORS.any { color -> lower.contains(color) }
            }
            if (matching.size >= 3) listOf(TraitGroup("Color in Title", "Contains a color", matching))
            else emptyList()
        }
        BookTrait.ANIMAL_IN_TITLE -> {
            val matching = pool.filter { dto ->
                val words = dto.volumeInfo.title.lowercase()
                    .split(Regex("[^a-z]+"))
                    .filter { it.isNotEmpty() }
                ANIMALS.any { animal -> animal in words }
            }
            if (matching.size >= 3) listOf(TraitGroup("Animal in Title", "Contains an animal", matching))
            else emptyList()
        }
        BookTrait.SINGLE_WORD_TITLE -> {
            val matching = pool.filter { it.volumeInfo.title.trim().split(" ").size == 1 }
            if (matching.size >= 3) listOf(TraitGroup("Single-Word Title", "One-word title", matching))
            else emptyList()
        }
        BookTrait.SAME_PUBLISHED_YEAR -> {
            pool.groupBy { it.volumeInfo.publishedDate?.take(4) ?: "" }
                .filter { it.key.isNotBlank() && it.key.length == 4 }
                .map { (year, books) -> TraitGroup("Same Published Year", "Published in $year", books) }
        }
        BookTrait.SAME_PUBLISHER -> {
            pool.groupBy { it.volumeInfo.publisher ?: "" }
                .filter { it.key.isNotBlank() }
                .map { (publisher, books) -> TraitGroup("Same Publisher", publisher, books) }
        }
    }
}

fun buildOddOneOutQuestion(
    sharedBooks: List<GoogleBooksVolumeDto>,
    oddOneOut: GoogleBooksVolumeDto,
    traitDisplayName: String,
    sharedValue: String
): Question? {
    val three = sharedBooks.take(3)
    val allFour = (three + oddOneOut).shuffled()

    val cards = allFour.mapNotNull { dto ->
        val cover = dto.volumeInfo.imageLinks?.thumbnail?.toHttps() ?: return@mapNotNull null
        BookCard(id = dto.id, title = dto.volumeInfo.title, coverUrl = cover)
    }
    if (cards.size < 4) return null

    return Question(
        id = "book_odd_one_out_${oddOneOut.id}",
        correctAnswer = oddOneOut.volumeInfo.title,
        content = QuestionContent.BookOddOneOut(
            books = cards,
            traitDisplayName = traitDisplayName,
            sharedValue = sharedValue
        )
    )
}

private fun String.normalizeGenre(): String =
    this.split("/").first().trim()

private fun String.toHttps(): String = replace("http://", "https://")
