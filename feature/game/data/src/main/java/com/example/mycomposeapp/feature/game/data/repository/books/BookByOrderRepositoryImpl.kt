package com.example.mycomposeapp.feature.game.data.repository.books

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.feature.game.data.seeder.StoryOrderPuzzleSeeder
import com.example.mycomposeapp.feature.game.domain.model.DailyPuzzle
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent
import com.example.mycomposeapp.feature.game.domain.model.BookEvent
import com.example.mycomposeapp.feature.game.domain.repository.DailyPuzzleRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

class BookByOrderRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val dataStore: DataStore<Preferences>,
) : DailyPuzzleRepository {

    override fun getDailyPuzzle(date: String): Flow<Resource<DailyPuzzle>> = flow {
        emit(Resource.Loading)
        try {
            val snapshot = firestore.collection("book_order")
                .whereEqualTo("date", date)
                .limit(1)
                .get()
                .await()

            if (snapshot.isEmpty) {
                emit(Resource.Error("No daily puzzle available for today"))
                return@flow
            }

            val doc = snapshot.documents.first()

            val puzzleId = doc.getLong("puzzleId") ?: 0L
            val bookTitle = doc.getString("bookTitle") ?: ""
            val docDate = doc.getString("date") ?: date
            val genre = doc.getString("genre") ?: ""
            val mainCharacter = doc.getString("mainCharacter") ?: ""
            val timeLimitSec = (doc.getLong("timeLimitSec") ?: 60L).toInt()

            val answer = (doc.get("answer") as? List<*>) // [1,2,3,4,5,6]
                ?.mapNotNull { (it as? Number)?.toInt() }
                ?: emptyList()

            val events = (doc.get("events") as? List<*>) // [{eventId:1,text:"..."}, ...]
                ?.mapNotNull { raw ->
                    val map = raw as? Map<*, *> ?: return@mapNotNull null
                    val eventId = (map["eventId"] as? Number)?.toInt() ?: return@mapNotNull null
                    val text = map["text"] as? String ?: return@mapNotNull null
                    BookEvent(eventId = eventId, text = text)
                }
                ?: emptyList()

            if (events.size != 6 || answer.size != 6) {
                emit(Resource.Error("Invalid puzzle data (expected 6 events and 6 answers)"))
                return@flow
            }

            val isCompleted = isDailyCompleted(docDate)

            emit(
                Resource.Success(
                    DailyPuzzle(
                        date = docDate,
                        question = Question(
                            id = "book_order_$puzzleId",
                            // This mode doesn't use text correctAnswer; keep it stable for feedback screens:
                            correctAnswer = answer.joinToString(","),
                            content = QuestionContent.BookByOrder(
                                puzzleId = puzzleId,
                                bookTitle = bookTitle,
                                date = docDate,
                                timeLimitSec = timeLimitSec,
                                genre = genre,
                                mainCharacter = mainCharacter,
                                events = events,
                                answer = answer,
                                isDaily = true
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
            val snapshot = firestore.collection("book_order")
                .whereLessThan("date", today)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(30)
                .get()
                .await()

            val puzzles = snapshot.documents.mapNotNull { doc ->
                val puzzleId = doc.getLong("puzzleId") ?: return@mapNotNull null
                val bookTitle = doc.getString("bookTitle") ?: return@mapNotNull null
                val docDate = doc.getString("date") ?: return@mapNotNull null
                val genre = doc.getString("genre") ?: ""
                val mainCharacter = doc.getString("mainCharacter") ?: ""
                val timeLimitSec = (doc.getLong("timeLimitSec") ?: 60L).toInt()

                val answer = (doc.get("answer") as? List<*>)
                    ?.mapNotNull { (it as? Number)?.toInt() }
                    ?: return@mapNotNull null

                val events = (doc.get("events") as? List<*>)
                    ?.mapNotNull { raw ->
                        val map = raw as? Map<*, *> ?: return@mapNotNull null
                        val eventId = (map["eventId"] as? Number)?.toInt() ?: return@mapNotNull null
                        val text = map["text"] as? String ?: return@mapNotNull null
                        BookEvent(eventId = eventId, text = text)
                    }
                    ?: return@mapNotNull null

                if (events.size != 6 || answer.size != 6) return@mapNotNull null

                val isCompleted = isDailyCompleted(docDate)

                DailyPuzzle(
                    date = docDate,
                    question = Question(
                        id = "book_order_$puzzleId",
                        correctAnswer = answer.joinToString(","),
                        content = QuestionContent.BookByOrder(
                            puzzleId = puzzleId,
                            bookTitle = bookTitle,
                            date = docDate,
                            timeLimitSec = timeLimitSec,
                            genre = genre,
                            mainCharacter = mainCharacter,
                            events = events,
                            answer = answer,
                            isDaily = false
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

    override suspend fun seedPuzzles() {
        val collection = firestore.collection("book_order")

        val batch = firestore.batch()

        StoryOrderPuzzleSeeder.ALL.forEach { puzzle ->
            val id = (puzzle["puzzleId"] as Long).toString()
            batch.set(collection.document(id), puzzle)
        }

        batch.commit().await()
    }

    private suspend fun isDailyCompleted(date: String): Boolean {
        val key = booleanPreferencesKey("daily_completed_$date")
        val prefs = dataStore.data.first()
        return prefs[key] == true
    }
}