package com.example.mycomposeapp.feature.profile.edit_profile.data.worker

import android.content.Context
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.mycomposeapp.feature.profile.edit_profile.domain.StorageKeys
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class UploadProfilePhotoWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val uriString = inputData.getString(StorageKeys.IMAGE_URI) ?: return Result.failure()
        val userId = inputData.getString(StorageKeys.USER_ID) ?: return Result.failure()

        val uri = uriString.toUri()

        return try {
            val storage = FirebaseStorage.getInstance()
            val firestore = FirebaseFirestore.getInstance()

            val ref = storage.reference.child("${StorageKeys.IMAGES_FOLDER}/$userId/avatar.jpg")

            ref.putFile(uri).await()
            val url = ref.downloadUrl.await().toString()

            firestore.collection("users")
                .document(userId)
                .update("photoUrl", url)
                .await()

            Result.success(
                workDataOf("downloadUrl" to url)
            )
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
