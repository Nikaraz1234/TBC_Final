package com.example.mycomposeapp.core.data.repository

import com.example.mycomposeapp.core.data.mapper.toAppNotificationOrNull
import com.example.mycomposeapp.core.data.mapper.toFirestoreMap
import com.example.mycomposeapp.core.domain.model.AppNotification
import com.example.mycomposeapp.core.domain.repository.NotificationsRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotificationsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : NotificationsRepository {

    private fun notifRef(userId: String) =
        firestore.collection("users")
            .document(userId)
            .collection("notifications")

    override fun observeAll(userId: String): Flow<List<AppNotification>> = callbackFlow {
        val reg: ListenerRegistration = notifRef(userId)
            .orderBy("createdAtMillis", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val items = snapshot?.documents.orEmpty().mapNotNull { doc ->
                    doc.toAppNotificationOrNull()
                }
                trySend(items)
            }

        awaitClose { reg.remove() }
    }

    override fun observeUnread(userId: String): Flow<List<AppNotification>> = callbackFlow {
        val reg: ListenerRegistration = notifRef(userId)
            .whereEqualTo("read", false)
            .orderBy("createdAtMillis", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val items = snapshot?.documents.orEmpty().mapNotNull { doc ->
                    doc.toAppNotificationOrNull()
                }
                trySend(items)
            }

        awaitClose { reg.remove() }
    }

    override suspend fun insert(notification: AppNotification) {
        val docId = notification.id.ifBlank { notifRef(notification.userId).document().id }

        notifRef(notification.userId)
            .document(docId)
            .set(notification.toFirestoreMap(docId))
            .await()
    }

    override suspend fun markAsRead(id: String, userId: String) {
        notifRef(userId)
            .document(id)
            .update("read", true)
            .await()
    }
    override suspend fun delete(id: String, userId: String) {
        notifRef(userId)
            .document(id)
            .delete()
            .await()
    }
}


