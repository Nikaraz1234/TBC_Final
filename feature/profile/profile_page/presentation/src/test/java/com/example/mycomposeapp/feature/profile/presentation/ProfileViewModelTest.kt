@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.mycomposeapp.feature.profile.presentation

import app.cash.turbine.test
import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.core.domain.model.User
import com.example.mycomposeapp.core.domain.usecase.auth.LogoutUseCase
import com.example.mycomposeapp.core.domain.usecase.datastore.RemovePreferenceUseCase
import com.example.mycomposeapp.core.domain.usecase.user.DeleteUserUseCase
import com.example.mycomposeapp.core.domain.usecase.user.GetCurrentUserUseCase
import com.example.mycomposeapp.feature.profile.presentation.ProfileContract.Event.*
import com.example.test_utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getCurrentUserUseCase: GetCurrentUserUseCase = mockk()
    private val logoutUseCase: LogoutUseCase = mockk()
    private val removePreferenceUseCase: RemovePreferenceUseCase = mockk(relaxed = true)
    private val deleteUserUseCase: DeleteUserUseCase = mockk()

    @Test
    fun `Given ViewModel init When user emits Then state user is updated`() = runTest {
        val userFlow = MutableSharedFlow<User?>()
        every { getCurrentUserUseCase() } returns userFlow

        val vm = createVm()

        vm.uiState.test {
            assertEquals(null, awaitItem().user)

            val fakeUser: User = mockk(relaxed = true)
            userFlow.emit(fakeUser)

            assertEquals(fakeUser, awaitItem().user)
        }
    }

    @Test
    fun `Given SettingsSheet hidden When OnSettingsClicked Then showSettingsSheet true`() = runTest {
        every { getCurrentUserUseCase() } returns MutableSharedFlow()

        val vm = createVm()

        vm.uiState.test {
            awaitItem()
            vm.onEvent(OnSettingsClicked)
            assertEquals(true, awaitItem().showSettingsSheet)
        }
    }

    @Test
    fun `Given SettingsSheet shown When OnSettingsDismissed Then showSettingsSheet false`() = runTest {
        every { getCurrentUserUseCase() } returns MutableSharedFlow()

        val vm = createVm()

        vm.uiState.test {
            awaitItem()
            vm.onEvent(OnSettingsClicked)
            awaitItem()
            vm.onEvent(OnSettingsDismissed)
            assertEquals(false, awaitItem().showSettingsSheet)
        }
    }

    @Test
    fun `Given ViewModel When OnEditProfileClicked Then GoToEditProfile side effect emitted`() = runTest {
        every { getCurrentUserUseCase() } returns MutableSharedFlow()

        val vm = createVm()

        vm.sideEffect.test {
            vm.onEvent(OnEditProfileClicked)
            assertEquals(ProfileContract.SideEffect.GoToEditProfile, awaitItem())
        }
    }

    @Test
    fun `Given ViewModel init When Load Then GetCurrentUserUseCase called twice`() = runTest {
        val userFlow = MutableSharedFlow<User?>()
        every { getCurrentUserUseCase() } returns userFlow

        val vm = createVm()

        vm.onEvent(Load)
        advanceUntilIdle()

        coVerify(exactly = 2) { getCurrentUserUseCase() }
    }

    @Test
    fun `Given LogoutUseCase Success When LogoutClicked Then navigates to welcome`() = runTest {
        every { getCurrentUserUseCase() } returns MutableSharedFlow()
        coEvery { logoutUseCase() } returns flowOf(Resource.Success(Unit))

        val vm = createVm()

        vm.sideEffect.test {
            vm.onEvent(LogoutClicked)
            advanceUntilIdle()
            assertEquals(ProfileContract.SideEffect.GoToWelcomeScreen, awaitItem())
        }
    }

    @Test
    fun `Given LogoutUseCase Error When LogoutClicked Then state error set`() = runTest {
        every { getCurrentUserUseCase() } returns MutableSharedFlow()
        val msg = "logout failed"

        coEvery { logoutUseCase() } returns flowOf(Resource.Error(msg))

        val vm = createVm()

        vm.uiState.test {
            awaitItem()
            vm.onEvent(LogoutClicked)
            advanceUntilIdle()
            assertEquals(msg, awaitItem().error)
        }
    }

    @Test
    fun `Given LogoutUseCase Loading When LogoutClicked Then isLoading true`() = runTest {
        every { getCurrentUserUseCase() } returns MutableSharedFlow()
        coEvery { logoutUseCase() } returns flowOf(Resource.Loading)

        val vm = createVm()

        val job = launch {
            vm.uiState.collect {}
        }

        vm.onEvent(LogoutClicked)
        advanceUntilIdle()

        assertEquals(true, vm.uiState.value.isLoading)

        job.cancel()
    }

    private fun createVm() = ProfileViewModel(
        getCurrentUserUseCase,
        logoutUseCase,
        removePreferenceUseCase,
        deleteUserUseCase
    )
}