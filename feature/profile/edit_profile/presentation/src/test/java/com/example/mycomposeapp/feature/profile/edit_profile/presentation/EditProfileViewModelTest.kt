package com.example.mycomposeapp.feature.profile.edit_profile.presentation

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.core.domain.model.User
import com.example.mycomposeapp.core.domain.usecase.user.GetCurrentUserUseCase
import com.example.mycomposeapp.feature.profile.edit_profile.domain.usecase.UploadProfilePhotoUseCase
import com.example.mycomposeapp.feature.profile.edit_profile.domain.usecase.user.ChangePasswordUseCase
import com.example.mycomposeapp.feature.profile.edit_profile.domain.usecase.user.ChangeUsernameUseCase
import com.example.mycomposeapp.feature.profile.edit_profile.domain.usecase.user.GetCurrentUserEmailUseCase
import com.example.test_utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EditProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getCurrentUserEmail: GetCurrentUserEmailUseCase = mockk()
    private val changePasswordUseCase: ChangePasswordUseCase = mockk()
    private val changeUsernameUseCase: ChangeUsernameUseCase = mockk()
    private val getUserUseCase: GetCurrentUserUseCase = mockk()
    private val uploadProfilePhotoUseCase: UploadProfilePhotoUseCase = mockk()

    private lateinit var viewModel: EditProfileViewModel

    @Before
    fun setup() {
        viewModel = EditProfileViewModel(
            getCurrentUserEmail,
            changePasswordUseCase,
            changeUsernameUseCase,
            getUserUseCase,
            uploadProfilePhotoUseCase
        )
    }

    @Test
    fun givenScreenOpened_whenUserExists_thenStateUpdated() = runTest {
        val user = User(userId = "1", username = "nika")

        every { getCurrentUserEmail() } returns "nika@gmail.com"
        every { getUserUseCase() } returns flowOf(user)

        viewModel.onEvent(EditProfileContract.Event.OnScreenOpened)
        advanceUntilIdle()

        assertEquals("nika@gmail.com", viewModel.uiState.value.email)
        assertEquals("nika", viewModel.uiState.value.username)
        assertEquals(user, viewModel.uiState.value.user)
        assertNull(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun givenScreenOpened_whenUserNull_thenErrorSet() = runTest {
        every { getCurrentUserEmail() } returns "email"
        every { getUserUseCase() } returns flowOf(null)

        viewModel.onEvent(EditProfileContract.Event.OnScreenOpened)
        advanceUntilIdle()

        assertEquals("User not found", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun givenBlankCurrentPassword_whenChangePassword_thenErrorShown() = runTest {
        viewModel.onEvent(EditProfileContract.Event.OnChangePasswordClicked)

        assertEquals("Enter current password", viewModel.uiState.value.error)
    }

    @Test
    fun givenBlankNewPassword_whenChangePassword_thenErrorShown() = runTest {
        viewModel.onEvent(EditProfileContract.Event.OnCurrentPasswordChanged("123"))
        viewModel.onEvent(EditProfileContract.Event.OnChangePasswordClicked)

        assertEquals("Enter new password and confirm it", viewModel.uiState.value.error)
    }

    @Test
    fun givenMismatchedPasswords_whenChangePassword_thenErrorShown() = runTest {
        viewModel.onEvent(EditProfileContract.Event.OnCurrentPasswordChanged("123"))
        viewModel.onEvent(EditProfileContract.Event.OnNewPasswordChanged("abc"))
        viewModel.onEvent(EditProfileContract.Event.OnConfirmPasswordChanged("xyz"))
        viewModel.onEvent(EditProfileContract.Event.OnChangePasswordClicked)

        assertEquals("Passwords do not match", viewModel.uiState.value.error)
    }

    @Test
    fun givenValidPasswords_whenChangePasswordSuccess_thenFieldsCleared() = runTest {
        coEvery { changePasswordUseCase(any(), any()) } returns
                flowOf(Resource.Success(Unit))

        viewModel.onEvent(EditProfileContract.Event.OnCurrentPasswordChanged("123"))
        viewModel.onEvent(EditProfileContract.Event.OnNewPasswordChanged("abc"))
        viewModel.onEvent(EditProfileContract.Event.OnConfirmPasswordChanged("abc"))
        viewModel.onEvent(EditProfileContract.Event.OnChangePasswordClicked)

        advanceUntilIdle()

        assertEquals("", viewModel.uiState.value.currentPassword)
        assertEquals("", viewModel.uiState.value.newPassword)
        assertEquals("", viewModel.uiState.value.confirmPassword)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun givenPasswordUseCaseFails_whenChangePassword_thenErrorSet() = runTest {
        coEvery { changePasswordUseCase(any(), any()) } returns
                flowOf(Resource.Error("fail"))

        viewModel.onEvent(EditProfileContract.Event.OnCurrentPasswordChanged("123"))
        viewModel.onEvent(EditProfileContract.Event.OnNewPasswordChanged("abc"))
        viewModel.onEvent(EditProfileContract.Event.OnConfirmPasswordChanged("abc"))
        viewModel.onEvent(EditProfileContract.Event.OnChangePasswordClicked)

        advanceUntilIdle()

        assertEquals("fail", viewModel.uiState.value.error)
    }

    @Test
    fun givenBlankUsername_whenChangeUsername_thenErrorShown() = runTest {
        viewModel.onEvent(EditProfileContract.Event.OnUsernameChanged("   "))
        viewModel.onEvent(EditProfileContract.Event.OnChangeUsernameClicked)

        assertEquals("Username cannot be empty", viewModel.uiState.value.error)
    }

    @Test
    fun givenValidUsername_whenChangeUsernameSuccess_thenUserUpdated() = runTest {
        val user = User(userId = "1", username = "old")

        every { getCurrentUserEmail() } returns "email"
        every { getUserUseCase() } returns flowOf(user)

        coEvery { changeUsernameUseCase(any()) } returns
                flowOf(Resource.Success(Unit))

        viewModel.onEvent(EditProfileContract.Event.OnScreenOpened)
        advanceUntilIdle()

        viewModel.onEvent(EditProfileContract.Event.OnUsernameChanged("new"))
        viewModel.onEvent(EditProfileContract.Event.OnChangeUsernameClicked)
        advanceUntilIdle()

        assertEquals("new", viewModel.uiState.value.user?.username)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun givenUsernameUseCaseFails_whenChangeUsername_thenErrorSet() = runTest {
        val user = User(userId = "1", username = "old")

        every { getCurrentUserEmail() } returns "email"
        every { getUserUseCase() } returns flowOf(user)

        coEvery { changeUsernameUseCase(any()) } returns
                flowOf(Resource.Error("error"))

        viewModel.onEvent(EditProfileContract.Event.OnScreenOpened)
        advanceUntilIdle()

        viewModel.onEvent(EditProfileContract.Event.OnUsernameChanged("new"))
        viewModel.onEvent(EditProfileContract.Event.OnChangeUsernameClicked)
        advanceUntilIdle()

        assertEquals("error", viewModel.uiState.value.error)
    }

    @Test
    fun givenNoUser_whenUploadPhoto_thenErrorShown() = runTest {
        viewModel.onEvent(EditProfileContract.Event.OnPhotoSelected("uri"))

        assertEquals("User not loaded", viewModel.uiState.value.error)
    }

    @Test
    fun givenUser_whenUploadPhotoSuccess_thenNoError() = runTest {
        val user = User(userId = "1", username = "nika")

        every { getCurrentUserEmail() } returns "email"
        every { getUserUseCase() } returns flowOf(user)

        coEvery { uploadProfilePhotoUseCase(any(), any()) } returns
                flowOf(Resource.Success(Unit))

        viewModel.onEvent(EditProfileContract.Event.OnScreenOpened)
        advanceUntilIdle()

        viewModel.onEvent(EditProfileContract.Event.OnPhotoSelected("uri"))
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun givenUploadFails_whenUploadPhoto_thenErrorSet() = runTest {
        val user = User(userId = "1", username = "nika")

        every { getCurrentUserEmail() } returns "email"
        every { getUserUseCase() } returns flowOf(user)

        coEvery { uploadProfilePhotoUseCase(any(), any()) } returns
                flowOf(Resource.Error("upload error"))

        viewModel.onEvent(EditProfileContract.Event.OnScreenOpened)
        advanceUntilIdle()

        viewModel.onEvent(EditProfileContract.Event.OnPhotoSelected("uri"))
        advanceUntilIdle()

        assertEquals("upload error", viewModel.uiState.value.error)
    }
}