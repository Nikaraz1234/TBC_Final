package com.example.mycomposeapp.feature.profile.edit_profile.data.repository

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.mycomposeapp.core.data.common.HandleResponse
import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.feature.profile.edit_profile.data.worker.UploadProfilePhotoWorker
import com.example.mycomposeapp.feature.profile.edit_profile.domain.StorageKeys
import com.example.mycomposeapp.core.domain.model.User
import com.example.mycomposeapp.feature.profile.edit_profile.domain.repository.ProfileRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val workManager: WorkManager,
    private val handleResponse: HandleResponse,
) : ProfileRepository {

    override fun uploadProfilePhoto(
        userId: String,
        uriString: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            val request = OneTimeWorkRequestBuilder<UploadProfilePhotoWorker>()
                .setInputData(
                    workDataOf(
                        StorageKeys.USER_ID to userId,
                        StorageKeys.IMAGE_URI to uriString
                    )
                )
                .build()

            workManager.enqueue(request)

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to start upload"))
        }
    }


    override fun updatePhotoUrl(userId: String, photoUrl: String): Flow<Resource<Unit>> =
        handleResponse.safeApiCall {
            firestore.collection("users")
                .document(userId)
                .update("photoUrl", photoUrl)
                .await()
            Unit
        }

    override fun observeUser(userId: String): Flow<Resource<User>> = callbackFlow {
        trySend(Resource.Loading)

        val reg = firestore.collection("users")
            .document(userId)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    trySend(Resource.Error(err.localizedMessage ?: "Failed to load user"))
                    return@addSnapshotListener
                }

                val user = snap?.toObject(User::class.java)
                if (user != null) {
                    trySend(Resource.Success(user))
                } else {
                    trySend(Resource.Error("User not found"))
                }
            }

        awaitClose { reg.remove() }
    }

}

