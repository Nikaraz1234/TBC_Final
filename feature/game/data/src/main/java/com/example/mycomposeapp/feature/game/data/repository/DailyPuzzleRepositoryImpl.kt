package com.example.mycomposeapp.feature.game.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.feature.game.data.remote.dto.EmojiPuzzleDto
import com.example.mycomposeapp.feature.game.domain.model.DailyPuzzle
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent
import com.example.mycomposeapp.feature.game.domain.repository.DailyPuzzleRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

class DailyPuzzleRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val dataStore: DataStore<Preferences>
) : DailyPuzzleRepository {

    override fun getDailyPuzzle(date: String): Flow<Resource<DailyPuzzle>> = flow {
        emit(Resource.Loading)
        try {
            val snapshot = firestore.collection("emoji_puzzles")
                .document(date)
                .get()
                .await()

            if (!snapshot.exists()) {
                emit(Resource.Error("No daily puzzle available for today"))
                return@flow
            }

            val dto = EmojiPuzzleDto(
                tmdbMovieId = (snapshot.getLong("tmdbMovieId") ?: 0).toInt(),
                movieTitle = snapshot.getString("movieTitle") ?: "",
                emojis = snapshot.getString("emojis") ?: "",
                mainActor = snapshot.getString("mainActor") ?: "",
                date = snapshot.getString("date") ?: date
            )

            val isCompleted = isDailyCompleted(date)
            emit(Resource.Success(dto.toDailyPuzzle(isCompleted, isFromArchive = false)))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to load daily puzzle"))
        }
    }

    override fun getArchivePuzzles(): Flow<Resource<List<DailyPuzzle>>> = flow {
        emit(Resource.Loading)
        try {
            val today = LocalDate.now().toString()
            val snapshot = firestore.collection("emoji_puzzles")
                .whereLessThan("date", today)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(30)
                .get()
                .await()

            val puzzles = snapshot.documents.mapNotNull { doc ->
                val dto = EmojiPuzzleDto(
                    tmdbMovieId = (doc.getLong("tmdbMovieId") ?: return@mapNotNull null).toInt(),
                    movieTitle = doc.getString("movieTitle") ?: return@mapNotNull null,
                    emojis = doc.getString("emojis") ?: return@mapNotNull null,
                    mainActor = doc.getString("mainActor") ?: "",
                    date = doc.getString("date") ?: return@mapNotNull null
                )

                val isCompleted = isDailyCompleted(dto.date)
                dto.toDailyPuzzle(isCompleted, isFromArchive = true)
            }

            emit(Resource.Success(puzzles))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to load archive"))
        }
    }

    override suspend fun markDailyCompleted(date: String) {
        val key = booleanPreferencesKey("daily_completed_$date")
        dataStore.edit { prefs ->
            prefs[key] = true
        }
    }

    private suspend fun isDailyCompleted(date: String): Boolean {
        val key = booleanPreferencesKey("daily_completed_$date")
        val prefs = dataStore.data.first()
        return prefs[key] == true
    }
}

private fun EmojiPuzzleDto.toDailyPuzzle(isCompleted: Boolean, isFromArchive: Boolean): DailyPuzzle {
    return DailyPuzzle(
        date = date,
        question = Question(
            id = "emoji_$tmdbMovieId",
            correctAnswer = movieTitle,
            content = QuestionContent.Emoji(
                emojiClues = emojis,
                isDaily = !isFromArchive,
                date = date,
                hintText = mainActor
            )
        ),
        isCompleted = isCompleted,
        isFromArchive = isFromArchive
    )
}
