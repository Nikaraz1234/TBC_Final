package com.example.mycomposeapp.core.domain.usecase.notification

import com.example.mycomposeapp.core.domain.repository.NotificationsRepository
import javax.inject.Inject

class ClearNotificationsUseCase @Inject constructor(
    private val repository: NotificationsRepository
) {
    suspend operator fun invoke() {
        repository.clearAll()
    }
}