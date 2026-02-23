package com.example.mycomposeapp.feature.game.data.repository.books

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.feature.game.data.mapper.books.buildOddOneOutQuestion
import com.example.mycomposeapp.feature.game.data.mapper.books.findTraitGroups
import com.example.mycomposeapp.feature.game.data.remote.books.GoogleBooksApiService
import com.example.mycomposeapp.feature.game.data.remote.books.dto.GoogleBooksVolumeDto
import com.example.mycomposeapp.feature.game.domain.constants.GameConstants
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.repository.books.BookOddOneOutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BookOddOneOutRepositoryImpl @Inject constructor(
    private val apiService: GoogleBooksApiService
) : BookOddOneOutRepository {

    override fun fetchOddOneOutBatch(
        batchSize: Int,
        startIndexMax: Int,
        excludeIds: Set<String>
    ): Flow<Resource<List<Question>>> = flow {
        emit(Resource.Loading)
        try {
            val startIndex = if (startIndexMax > 0) (0..startIndexMax).random() else 0
            val pool = fetchValidBooks(startIndex, excludeIds)

            if (pool.size < 4) {
                emit(Resource.Error("Not enough books to build puzzles"))
                return@flow
            }

            val traitGroups = findTraitGroups(pool).shuffled()
            val questions = mutableListOf<Question>()
            val usedBookIds = mutableSetOf<String>()

            for ((_, group) in traitGroups) {
                if (questions.size >= batchSize) break
                val available = group.matching.filter { it.id !in usedBookIds }
                if (available.size < 3) continue

                val outsiders = pool.filter { it.id !in group.matching.map { m -> m.id } && it.id !in usedBookIds }
                if (outsiders.isEmpty()) continue

                val sharedThree = available.shuffled().take(3)
                val oddOne = outsiders.random()

                val question = buildOddOneOutQuestion(
                    sharedBooks = sharedThree,
                    oddOneOut = oddOne,
                    traitDisplayName = group.traitDisplayName,
                    sharedValue = group.sharedValue
                ) ?: continue

                questions.add(question)
                usedBookIds.addAll(sharedThree.map { it.id })
                usedBookIds.add(oddOne.id)
            }

            if (questions.isEmpty()) {
                emit(Resource.Error("Could not build odd-one-out puzzles"))
            } else {
                emit(Resource.Success(questions))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to fetch odd-one-out questions"))
        }
    }

    private suspend fun fetchValidBooks(
        startIndex: Int,
        excludeIds: Set<String>
    ): List<GoogleBooksVolumeDto> {
        val poolSize = GameConstants.BOOK_ODD_ONE_OUT_POOL_SIZE
        val results = mutableListOf<GoogleBooksVolumeDto>()

        val queries = listOf("subject:fiction", "subject:nonfiction", "bestseller")
        for (query in queries) {
            if (results.size >= poolSize) break
            try {
                val batch = apiService.searchVolumes(
                    query = query,
                    startIndex = startIndex,
                    maxResults = 40
                ).items.filter { dto ->
                    dto.id !in excludeIds &&
                    dto.volumeInfo.imageLinks?.thumbnail != null &&
                    dto.volumeInfo.title.isNotBlank()
                }
                results.addAll(batch)
            } catch (_: Exception) { }
        }
        return results.distinctBy { it.id }.take(poolSize)
    }
}
