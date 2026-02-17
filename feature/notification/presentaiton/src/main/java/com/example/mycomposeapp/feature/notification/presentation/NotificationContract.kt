package com.example.mycomposeapp.feature.notification.presentation

import com.example.mycomposeapp.feature.notification.presentation.model.NotificationUi

object NotificationContract {

    data class State(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,

        val newNotifications: List<NotificationUi> = emptyList(),
        val olderNotifications: List<NotificationUi> = emptyList(),

        val unreadCount: Int = 0,
        val isEmpty: Boolean = true
    )

    sealed interface Event {
        data object ScreenShown : Event
        data object Refresh : Event

        data class NotificationClicked(
            val id: String
        ) : Event

        data class MarkAsRead(val id: String) : Event
    }

    sealed interface SideEffect {
        data class ShowMessage(val message: String) : SideEffect
    }
}
