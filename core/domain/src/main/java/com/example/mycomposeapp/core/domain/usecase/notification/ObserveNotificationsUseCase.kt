package com.example.mycomposeapp.core.domain.usecase.notification

import com.example.mycomposeapp.core.domain.model.AppNotification
import com.example.mycomposeapp.core.domain.repository.NotificationsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveNotificationsUseCase @Inject constructor(
    private val repository: NotificationsRepository
) {
    operator fun invoke(userId: String): Flow<List<AppNotification>> =
        repository.observeAll(userId)
}