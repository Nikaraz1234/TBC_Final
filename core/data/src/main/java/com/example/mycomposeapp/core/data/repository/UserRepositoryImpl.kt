package com.example.mycomposeapp.core.data.repository

import com.example.mycomposeapp.core.domain.model.User
import com.example.mycomposeapp.core.domain.model.UserStats
import com.example.mycomposeapp.core.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : UserRepository {

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                trySend(null)
            } else {
                firestore.collection("users")
                    .document(firebaseUser.uid)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            trySend(createDefaultUser(firebaseUser.uid, firebaseUser.displayName, firebaseUser.photoUrl?.toString()))
                            return@addSnapshotListener
                        }

                        val user = if (snapshot != null && snapshot.exists()) {
                            val stats = snapshot.get("stats") as? Map<*, *>
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
                                    currentStreak = (stats?.get("currentStreak") as? Long)?.toInt() ?: 0,
                                    highScore = (stats?.get("highScore") as? Long)?.toInt() ?: 0,
                                    achievements = (stats?.get("achievements") as? Long)?.toInt() ?: 0
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

        val docRef = firestore.collection("users").document(firebaseUser.uid)
        val snapshot = docRef.get().await()

        if (!snapshot.exists()) {
            val defaultUserData = mapOf(
                "username" to (firebaseUser.displayName ?: "Player"),
                "photoUrl" to firebaseUser.photoUrl?.toString(),
                "stats" to mapOf(
                    "coins" to 0,
                    "level" to 1,
                    "points" to 0,
                    "gamesPlayed" to 0,
                    "correctAnswers" to 0,
                    "bestStreak" to 0,
                    "currentStreak" to 0,
                    "highScore" to 0,
                    "achievements" to 0
                )
            )
            docRef.set(defaultUserData).await()
        }
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
