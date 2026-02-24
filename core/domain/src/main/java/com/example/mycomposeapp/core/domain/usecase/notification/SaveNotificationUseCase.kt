package com.example.mycomposeapp.core.domain.usecase.notification

import com.example.mycomposeapp.core.domain.model.AppNotification
import com.example.mycomposeapp.core.domain.repository.NotificationsRepository
import javax.inject.Inject

class SaveNotificationUseCase @Inject constructor(
    private val repository: NotificationsRepository
) {
    suspend operator fun invoke(notification: AppNotification) {
        repository.insert(notification)
    }
}