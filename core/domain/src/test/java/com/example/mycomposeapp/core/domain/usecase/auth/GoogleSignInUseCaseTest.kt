package com.example.mycomposeapp.core.domain.usecase.auth

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.core.domain.keys.PreferenceKeys
import com.example.mycomposeapp.core.domain.repository.AuthRepository
import com.example.mycomposeapp.core.domain.repository.DataStoreManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GoogleSignInUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var useCase: GoogleSignInUseCase

    @Before
    fun setUp() {
        authRepository = mockk()
        dataStoreManager = mockk(relaxUnitFun = true)
        useCase = GoogleSignInUseCase(authRepository, dataStoreManager)
    }

    // ==================== Success Cases ====================

    @Test
    fun `invoke returns Success and saves token when sign in succeeds`() = runTest {
        // Given
        val idToken = "google_id_token_123"
        val authToken = "auth_token_abc"
        coEvery { authRepository.signInWithGoogle(idToken) } returns Resource.Success(authToken)

        // When
        val result = useCase(idToken)

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(authToken, (result as Resource.Success).data)
        coVerify(exactly = 1) { dataStoreManager.setPreference(PreferenceKeys.TOKEN, authToken) }
    }

    @Test
    fun `invoke saves correct token to DataStore on success`() = runTest {
        // Given
        val idToken = "id_token"
        val authToken = "saved_token_xyz"
        coEvery { authRepository.signInWithGoogle(idToken) } returns Resource.Success(authToken)

        // When
        useCase(idToken)

        // Then
        coVerify(exactly = 1) { dataStoreManager.setPreference(PreferenceKeys.TOKEN, authToken) }
    }

    @Test
    fun `invoke returns Success with empty token when repository returns empty`() = runTest {
        // Given
        val idToken = "id_token"
        coEvery { authRepository.signInWithGoogle(idToken) } returns Resource.Success("")

        // When
        val result = useCase(idToken)

        // Then
        assertTrue(result is Resource.Success)
        assertEquals("", (result as Resource.Success).data)
        coVerify(exactly = 1) { dataStoreManager.setPreference(PreferenceKeys.TOKEN, "") }
    }

    // ==================== Error Cases ====================

    @Test
    fun `invoke returns Error when sign in fails`() = runTest {
        // Given
        val idToken = "google_id_token"
        val errorMessage = "Authentication failed"
        coEvery { authRepository.signInWithGoogle(idToken) } returns Resource.Error(errorMessage)

        // When
        val result = useCase(idToken)

        // Then
        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, (result as Resource.Error).message)
    }

    @Test
    fun `invoke does not save token when sign in fails`() = runTest {
        // Given
        val idToken = "google_id_token"
        coEvery { authRepository.signInWithGoogle(idToken) } returns Resource.Error("Failed")

        // When
        useCase(idToken)

        // Then
        coVerify(exactly = 0) { dataStoreManager.setPreference(any(), any<String>()) }
    }

    @Test
    fun `invoke returns Error with network error message`() = runTest {
        // Given
        val idToken = "id_token"
        val errorMessage = "Network error: Unable to connect"
        coEvery { authRepository.signInWithGoogle(idToken) } returns Resource.Error(errorMessage)

        // When
        val result = useCase(idToken)

        // Then
        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, (result as Resource.Error).message)
    }

    @Test
    fun `invoke returns Error with invalid token message`() = runTest {
        // Given
        val idToken = "invalid_token"
        val errorMessage = "Invalid Google ID token"
        coEvery { authRepository.signInWithGoogle(idToken) } returns Resource.Error(errorMessage)

        // When
        val result = useCase(idToken)

        // Then
        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, (result as Resource.Error).message)
    }

    // ==================== Loading Cases ====================

    @Test
    fun `invoke returns Loading when repository returns Loading`() = runTest {
        // Given
        val idToken = "google_id_token"
        coEvery { authRepository.signInWithGoogle(idToken) } returns Resource.Loading

        // When
        val result = useCase(idToken)

        // Then
        assertTrue(result is Resource.Loading)
    }

    @Test
    fun `invoke does not save token when Loading`() = runTest {
        // Given
        val idToken = "google_id_token"
        coEvery { authRepository.signInWithGoogle(idToken) } returns Resource.Loading

        // When
        useCase(idToken)

        // Then
        coVerify(exactly = 0) { dataStoreManager.setPreference(any(), any<String>()) }
    }

    // ==================== Input Variations ====================

    @Test
    fun `invoke passes correct idToken to repository`() = runTest {
        // Given
        val idToken = "specific_google_token_456"
        coEvery { authRepository.signInWithGoogle(idToken) } returns Resource.Success("token")

        // When
        useCase(idToken)

        // Then
        coVerify(exactly = 1) { authRepository.signInWithGoogle(idToken) }
    }

    @Test
    fun `invoke with empty idToken passes it to repository`() = runTest {
        // Given
        val idToken = ""
        coEvery { authRepository.signInWithGoogle(idToken) } returns Resource.Error("Empty token")

        // When
        val result = useCase(idToken)

        // Then
        coVerify(exactly = 1) { authRepository.signInWithGoogle("") }
        assertTrue(result is Resource.Error)
    }

    @Test
    fun `invoke with long idToken passes it to repository`() = runTest {
        // Given
        val idToken = "a".repeat(1000)
        coEvery { authRepository.signInWithGoogle(idToken) } returns Resource.Success("token")

        // When
        val result = useCase(idToken)

        // Then
        coVerify(exactly = 1) { authRepository.signInWithGoogle(idToken) }
        assertTrue(result is Resource.Success)
    }

    // ==================== Interaction Verification ====================

    @Test
    fun `invoke calls repository exactly once`() = runTest {
        // Given
        val idToken = "token"
        coEvery { authRepository.signInWithGoogle(idToken) } returns Resource.Success("auth")

        // When
        useCase(idToken)

        // Then
        coVerify(exactly = 1) { authRepository.signInWithGoogle(idToken) }
    }

    @Test
    fun `invoke calls setPreference exactly once on success`() = runTest {
        // Given
        val idToken = "token"
        coEvery { authRepository.signInWithGoogle(idToken) } returns Resource.Success("auth")

        // When
        useCase(idToken)

        // Then
        coVerify(exactly = 1) { dataStoreManager.setPreference(PreferenceKeys.TOKEN, "auth") }
    }
}