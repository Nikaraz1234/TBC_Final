package com.example.mycomposeapp.feature.achievements.data.repository

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.feature.achievements.data.dto.AchievementDto
import com.example.mycomposeapp.feature.achievements.data.mapper.toDomain
import com.example.mycomposeapp.feature.achievements.domain.model.AppAchievement
import com.example.mycomposeapp.feature.achievements.domain.repository.AchievementRepository
import com.example.mycomposeapp.feature.achievements.data.seeder.AchievementSeeder
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AchievementRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AchievementRepository {

    override fun getAchievements(): Flow<Resource<List<AppAchievement>>> = callbackFlow {
        trySend(Resource.Loading)

        val listener = firestore.collection("achievements")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Failed to fetch achievements"))
                    return@addSnapshotListener
                }
                val achievements = snapshot?.documents
                    ?.mapNotNull { doc ->
                        doc.toObject(AchievementDto::class.java)?.copy(id = doc.id)
                    }
                    ?.map { it.toDomain() }
                    ?: emptyList()

                trySend(Resource.Success(achievements))
            }

        awaitClose { listener.remove() }
    }

    override suspend fun seedAchievements() {
        val batch = firestore.batch()
        val collection = firestore.collection("achievements")
        AchievementSeeder.ALL.forEach { dto ->
            val docRef = collection.document(dto.id)
            val data = mapOf(
                "name"           to dto.name,
                "description"    to dto.description,
                "icon"           to dto.icon,
                "category"       to dto.category,
                "xpReward"       to dto.xpReward,
                "conditionType"  to dto.conditionType,
                "conditionValue" to dto.conditionValue,
                "conditionKey"   to dto.conditionKey
            )
            batch.set(docRef, data)
        }
        batch.commit().await()
    }
}
