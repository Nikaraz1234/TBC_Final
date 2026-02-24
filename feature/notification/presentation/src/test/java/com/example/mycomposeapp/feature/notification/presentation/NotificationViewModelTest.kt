package com.example.mycomposeapp.feature.notification.presentation

import com.example.test_utils.MainDispatcherRule
import com.example.mycomposeapp.core.domain.model.AppNotification
import com.example.mycomposeapp.core.domain.model.User
import com.example.mycomposeapp.core.domain.usecase.notification.DeleteNotificationUseCase
import com.example.mycomposeapp.core.domain.usecase.notification.MarkNotificationReadUseCase
import com.example.mycomposeapp.core.domain.usecase.notification.ObserveNotificationsUseCase
import com.example.mycomposeapp.core.domain.usecase.user.GetCurrentUserUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeNotifications: ObserveNotificationsUseCase = mockk()
    private val markRead: MarkNotificationReadUseCase = mockk()
    private val getCurrentUserUseCase: GetCurrentUserUseCase = mockk()
    private val deleteNotificationUseCase: DeleteNotificationUseCase = mockk()

    private lateinit var viewModel: NotificationViewModel

    @Before
    fun setup() {
        viewModel = NotificationViewModel(
            observeNotifications,
            markRead,
            getCurrentUserUseCase,
            deleteNotificationUseCase
        )
    }

    @Test
    fun givenNotifications_whenScreenShown_thenStateUpdated() = runTest {
        val user = User(userId = "1", username = "nika")

        val unread = AppNotification(id = "1", userId = "1", title = "", body = "", type = "", isRead = false, createdAtMillis = 1, deeplink = "")
        val read = unread.copy(id = "2", isRead = true)

        every { getCurrentUserUseCase() } returns flowOf(user)
        every { observeNotifications("1") } returns flowOf(listOf(unread, read))

        viewModel.onEvent(NotificationContract.Event.ScreenShown)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(1, viewModel.uiState.value.unreadCount)
        assertEquals(1, viewModel.uiState.value.newNotifications.size)
        assertEquals(1, viewModel.uiState.value.olderNotifications.size)
        assertFalse(viewModel.uiState.value.isEmpty)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun givenEmptyNotifications_whenScreenShown_thenIsEmptyTrue() = runTest {
        val user = User(userId = "1", username = "nika")

        every { getCurrentUserUseCase() } returns flowOf(user)
        every { observeNotifications("1") } returns flowOf(emptyList())

        viewModel.onEvent(NotificationContract.Event.ScreenShown)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isEmpty)
        assertEquals(0, viewModel.uiState.value.unreadCount)
    }

    @Test
    fun givenObserveFails_whenScreenShown_thenErrorSet() = runTest {
        val user = User(userId = "1", username = "nika")

        every { getCurrentUserUseCase() } returns flowOf(user)
        every { observeNotifications("1") } returns flow {
            throw RuntimeException("error")
        }

        viewModel.onEvent(NotificationContract.Event.ScreenShown)
        advanceUntilIdle()

        assertEquals("error", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun givenDeleteSuccess_whenDeleteEvent_thenSideEffectSent() = runTest {
        val user = User(userId = "1", username = "nika")

        every { getCurrentUserUseCase() } returns flowOf(user)
        coEvery { deleteNotificationUseCase("1", "1") } returns Unit

        viewModel.onEvent(NotificationContract.Event.Delete("1"))
        advanceUntilIdle()

        coVerify { deleteNotificationUseCase("1", "1") }
    }

    @Test
    fun givenDeleteFails_whenDeleteEvent_thenErrorSet() = runTest {
        val user = User(userId = "1", username = "nika")

        every { getCurrentUserUseCase() } returns flowOf(user)
        coEvery { deleteNotificationUseCase("1", "1") } throws RuntimeException("fail")

        viewModel.onEvent(NotificationContract.Event.Delete("1"))
        advanceUntilIdle()

        assertEquals("fail", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun givenMarkAsRead_whenEventTriggered_thenUseCaseCalled() = runTest {
        val user = User(userId = "1", username = "nika")

        every { getCurrentUserUseCase() } returns flowOf(user)
        coEvery { markRead("1", "1") } returns Unit

        viewModel.onEvent(NotificationContract.Event.MarkAsRead("1"))
        advanceUntilIdle()

        coVerify { markRead("1", "1") }
    }
}