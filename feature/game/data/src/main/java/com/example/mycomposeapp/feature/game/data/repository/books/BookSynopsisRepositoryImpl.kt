package com.example.mycomposeapp.feature.game.data.repository.books

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.feature.game.data.mapper.books.buildSynopsisQuestion
import com.example.mycomposeapp.feature.game.data.remote.books.GoogleBooksApiService
import com.example.mycomposeapp.feature.game.data.remote.books.dto.GoogleBooksVolumeDto
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.repository.books.BookSynopsisRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BookSynopsisRepositoryImpl @Inject constructor(
    private val apiService: GoogleBooksApiService
) : BookSynopsisRepository {

    override fun fetchSynopsisBatch(
        batchSize: Int,
        startIndexMax: Int,
        excludeIds: Set<String>
    ): Flow<Resource<List<Question>>> = flow {
        emit(Resource.Loading)
        try {
            val startIndex = if (startIndexMax > 0) (0..startIndexMax).random() else 0
            val targetBooks = fetchValidBooks(
                query = "subject:fiction",
                startIndex = startIndex,
                maxResults = 40,
                excludeIds = excludeIds
            )

            if (targetBooks.isEmpty()) {
                emit(Resource.Error("Not enough books available"))
                return@flow
            }

            // Fetch a pool of same-genre books to use as fake options
            val genrePool = fetchValidBooks(
                query = "subject:fiction",
                startIndex = (0..(startIndexMax + 20)).random(),
                maxResults = 40,
                excludeIds = emptySet()
            ).filter { it.id !in excludeIds }

            val questions = mutableListOf<Question>()
            val shuffledTargets = targetBooks.shuffled()

            for (target in shuffledTargets) {
                if (questions.size >= batchSize) break
                val fakePool = (genrePool + targetBooks)
                    .filter { it.id != target.id }
                    .distinctBy { it.id }
                    .shuffled()
                val question = buildSynopsisQuestion(target, fakePool) ?: continue
                questions.add(question)
            }

            if (questions.isEmpty()) {
                emit(Resource.Error("Could not build synopsis questions"))
            } else {
                emit(Resource.Success(questions))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to fetch book synopsis questions"))
        }
    }

    private suspend fun fetchValidBooks(
        query: String,
        startIndex: Int,
        maxResults: Int,
        excludeIds: Set<String>
    ): List<GoogleBooksVolumeDto> {
        return try {
            apiService.searchVolumes(
                query = query,
                startIndex = startIndex,
                maxResults = maxResults
            ).items.filter { dto ->
                dto.id !in excludeIds &&
                dto.volumeInfo.imageLinks?.thumbnail != null &&
                !dto.volumeInfo.description.isNullOrBlank() &&
                dto.volumeInfo.title.isNotBlank()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
