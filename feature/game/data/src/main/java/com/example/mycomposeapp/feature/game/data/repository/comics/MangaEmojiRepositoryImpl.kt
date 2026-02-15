package com.example.mycomposeapp.feature.game.data.repository.comics

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.mycomposeapp.core.domain.Resource
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

class MangaEmojiRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val dataStore: DataStore<Preferences>,
) : DailyPuzzleRepository {

    override fun getDailyPuzzle(date: String): Flow<Resource<DailyPuzzle>> = flow {
        emit(Resource.Loading)
        try {
            val snapshot = firestore.collection("anime_emoji")
                .whereEqualTo("date", date)
                .limit(1)
                .get()
                .await()


            if (snapshot.isEmpty()) {
                emit(Resource.Error("No daily puzzle available for today"))
                return@flow
            }
            val doc = snapshot.documents.first()

            val malAnimeId = (doc.getLong("malAnimeId") ?: 0L).toInt()
            val title = doc.getString("movieTitle") ?: ""
            val emojis = doc.getString("emojis") ?: ""
            val hint = doc.getString("mainActor") ?: ""
            val docDate = doc.getString("date") ?: date

            val isCompleted = isDailyCompleted(docDate)

            emit(
                Resource.Success(
                    DailyPuzzle(
                        date = docDate,
                        question = Question(
                            id = "anime_emoji_$malAnimeId",
                            correctAnswer = title,
                            content = QuestionContent.Emoji(
                                emojiClues = emojis,
                                isDaily = true,
                                date = docDate,
                                hintText = hint,
                            )
                        ),
                        isCompleted = isCompleted,
                        isFromArchive = false
                    )
                )
            )
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to load daily puzzle"))
        }
    }

    override fun getArchivePuzzles(): Flow<Resource<List<DailyPuzzle>>> = flow {
        emit(Resource.Loading)
        try {
            val today = LocalDate.now().toString()
            val snapshot = firestore.collection("anime_emoji")
                .whereLessThan("date", today)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(30)
                .get()
                .await()

            val puzzles = snapshot.documents.mapNotNull { doc ->
                val malAnimeId = (doc.getLong("malAnimeId") ?: return@mapNotNull null).toInt()
                val title = doc.getString("movieTitle") ?: return@mapNotNull null
                val emojis = doc.getString("emojis") ?: return@mapNotNull null
                val hint = doc.getString("mainActor") ?: ""
                val docDate = doc.getString("date") ?: return@mapNotNull null

                val isCompleted = isDailyCompleted(docDate)

                DailyPuzzle(
                    date = docDate,
                    question = Question(
                        id = "anime_emoji_$malAnimeId",
                        correctAnswer = title,
                        content = QuestionContent.Emoji(
                            emojiClues = emojis,
                            isDaily = false,
                            date = docDate,
                            hintText = hint,
                        )
                    ),
                    isCompleted = isCompleted,
                    isFromArchive = true
                )
            }

            emit(Resource.Success(puzzles))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to load archive"))
        }
    }

    override suspend fun markDailyCompleted(date: String) {
        val key = booleanPreferencesKey("daily_completed_$date")
        dataStore.edit { prefs -> prefs[key] = true }
    }

    private suspend fun isDailyCompleted(date: String): Boolean {
        val key = booleanPreferencesKey("daily_completed_$date")
        val prefs = dataStore.data.first()
        return prefs[key] == true
    }
}

