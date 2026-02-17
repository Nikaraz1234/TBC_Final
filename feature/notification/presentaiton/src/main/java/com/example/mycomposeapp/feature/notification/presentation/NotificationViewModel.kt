package com.example.mycomposeapp.feature.notification.presentation

import androidx.lifecycle.viewModelScope
import com.example.mycomposeapp.core.domain.usecase.notification.MarkNotificationReadUseCase
import com.example.mycomposeapp.core.domain.usecase.notification.ObserveNotificationsUseCase
import com.example.mycomposeapp.core.presentation.common.BaseViewModel
import com.example.mycomposeapp.feature.notification.presentation.mapper.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val observeNotifications: ObserveNotificationsUseCase,
    private val markRead: MarkNotificationReadUseCase,
) : BaseViewModel<
        NotificationContract.State,
        NotificationContract.SideEffect,
        NotificationContract.Event>(
    initialState = NotificationContract.State()
) {
    fun onEvent(event: NotificationContract.Event){
        when(event){
            is NotificationContract.Event.MarkAsRead -> markAsRead(event.id)
            is NotificationContract.Event.NotificationClicked -> {}
            NotificationContract.Event.Refresh -> TODO()
            NotificationContract.Event.ScreenShown -> observeInbox()
        }

    }

    private fun markAsRead(id: String){
        viewModelScope.launch {
            markRead(id)
        }
    }

    private fun observeInbox() {
        viewModelScope.launch {
             setState{ copy(isLoading = true, errorMessage = null) }

            observeNotifications()
                .catch { e ->
                    setState { copy(isLoading = false, errorMessage = e.message ?: "Unknown error") }
                }
                .collectLatest { list ->
                    val (newOnes, olderOnes) = list.partition { !it.isRead }

                    setState {
                        copy(
                            isLoading = false,
                            newNotifications = newOnes.map { it.toPresentation() },
                            olderNotifications = olderOnes.map { it.toPresentation() },
                            unreadCount = newOnes.size,
                            isEmpty = list.isEmpty()
                        )
                    }
                }
        }
    }

}