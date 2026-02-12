package com.example.mycomposeapp.core.data.repository

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.core.domain.model.User
import com.example.mycomposeapp.core.domain.model.UserStats
import com.example.mycomposeapp.core.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : UserRepository {

    companion object {
        private const val USERS_COLLECTION = "users"
    }

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                trySend(null)
            } else {
                firestore.collection(USERS_COLLECTION)
                    .document(firebaseUser.uid)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            trySend(createDefaultUser(firebaseUser.uid, firebaseUser.displayName, firebaseUser.photoUrl?.toString()))
                            return@addSnapshotListener
                        }

                        val user = if (snapshot != null && snapshot.exists()) {
                            val stats = snapshot.get("stats") as? Map<*, *>
                            val currentStreakRaw = stats?.get("currentStreak")
                            val highScoreRaw = stats?.get("highScore")
                            val achievementsRaw = stats?.get("achievements")

                            val currentStreak = mapToIntMap(currentStreakRaw)
                            val highScore = mapToIntMap(highScoreRaw)
                            val achievements = mapToStringList(achievementsRaw)

                            User(
                                userId = firebaseUser.uid,
                                username = snapshot.getString("username") ?: firebaseUser.displayName ?: "Player",
                                photoUrl = snapshot.getString("photoUrl") ?: firebaseUser.photoUrl?.toString(),
                                stats = UserStats(
                                    coins = (stats?.get("coins") as? Long)?.toInt() ?: 0,
                                    level = (stats?.get("level") as? Long)?.toInt() ?: 1,
                                    points = (stats?.get("points") as? Long)?.toInt() ?: 0,
                                    gamesPlayed = (stats?.get("gamesPlayed") as? Long)?.toInt() ?: 0,
                                    correctAnswers = (stats?.get("correctAnswers") as? Long)?.toInt() ?: 0,
                                    bestStreak = (stats?.get("bestStreak") as? Long)?.toInt() ?: 0,
                                    currentStreak = currentStreak,
                                    highScore = highScore,
                                    achievements = achievements,
                                    lastEmojiDate = (stats?.get("lastEmojiDate") as? String) ?: ""
                                )
                            )
                        } else {
                            createDefaultUser(firebaseUser.uid, firebaseUser.displayName, firebaseUser.photoUrl?.toString())
                        }
                        trySend(user)
                    }
            }
        }

        firebaseAuth.addAuthStateListener(authStateListener)

        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }

    override suspend fun refreshUser() {
        val firebaseUser = firebaseAuth.currentUser ?: return

        val docRef = firestore.collection(USERS_COLLECTION).document(firebaseUser.uid)
        val snapshot = docRef.get().await()

        if (!snapshot.exists()) {
            val defaultUserData = mapOf(
                "userId" to firebaseUser.uid,
                "username" to (firebaseUser.displayName ?: "Player"),
                "photoUrl" to firebaseUser.photoUrl?.toString(),
                "stats" to mapOf(
                    "coins" to 0,
                    "level" to 1,
                    "points" to 0,
                    "gamesPlayed" to 0,
                    "correctAnswers" to 0,
                    "bestStreak" to 0,
                    "currentStreak" to emptyMap<String, Int>(),
                    "highScore" to emptyMap<String, Int>(),
                    "achievements" to emptyList<String>()
                )
            )
            docRef.set(defaultUserData).await()
        }
    }

    override suspend fun updateUserStats(stats: UserStats) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        val statsMap = mapOf(
            "coins" to stats.coins,
            "level" to stats.level,
            "points" to stats.points,
            "gamesPlayed" to stats.gamesPlayed,
            "correctAnswers" to stats.correctAnswers,
            "bestStreak" to stats.bestStreak,
            "currentStreak" to stats.currentStreak,
            "highScore" to stats.highScore,
            "achievements" to stats.achievements,
            "lastEmojiDate" to stats.lastEmojiDate
        )
        firestore.collection(USERS_COLLECTION).document(uid)
            .update("stats", statsMap)
            .await()
    }

    override suspend fun updateCoins(coins: Int) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        firestore.collection(USERS_COLLECTION).document(uid)
            .update("stats.coins", coins)
            .await()
    }

    override fun changeUsername(newUsername: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)

        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            emit(Resource.Error("Not authenticated"))
            return@flow
        }

        try {
            firestore.collection("users")
                .document(firebaseUser.uid)
                .update("username", newUsername.trim())
                .await()

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to change username"))
        }
    }

    private fun mapToIntMap(raw: Any?): Map<String, Int> = when (raw) {
        is Map<*, *> -> raw.entries.mapNotNull { (k, v) ->
            val key = k as? String ?: return@mapNotNull null
            val value = (v as? Long)?.toInt() ?: return@mapNotNull null
            key to value
        }.toMap()
        else -> emptyMap()
    }

    private fun mapToStringList(raw: Any?): List<String> = when (raw) {
        is List<*> -> raw.filterIsInstance<String>()
        else -> emptyList()
    }

    private fun createDefaultUser(userId: String, displayName: String?, photoUrl: String?): User {
        return User(
            userId = userId,
            username = displayName ?: "Player",
            photoUrl = photoUrl,
            stats = UserStats()
        )
    }
}
