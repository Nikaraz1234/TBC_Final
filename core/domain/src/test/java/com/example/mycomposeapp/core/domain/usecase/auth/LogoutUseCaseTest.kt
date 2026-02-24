package com.example.mycomposeapp.core.domain.usecase.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.example.mycomposeapp.core.domain.repository.DataStoreManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RemovePreferenceUseCaseTest {

    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var useCase: RemovePreferenceUseCase

    @Before
    fun setUp() {
        dataStoreManager = mockk()
        useCase = RemovePreferenceUseCase(dataStoreManager)
    }

    // ==================== Basic Functionality ====================

    @Test
    fun `invoke calls removePreferences on dataStoreManager`() = runTest {
        // Given
        val keys = listOf(stringPreferencesKey("token"))
        coEvery { dataStoreManager.removePreferences(any()) } returns Unit

        // When
        useCase(keys)

        // Then
        coVerify(exactly = 1) { dataStoreManager.removePreferences(keys) }
    }

    @Test
    fun `invoke passes correct keys to repository`() = runTest {
        // Given
        val slot = slot<List<Preferences.Key<*>>>()
        val key1 = stringPreferencesKey("token")
        val key2 = intPreferencesKey("userId")
        val keys = listOf(key1, key2)
        coEvery { dataStoreManager.removePreferences(capture(slot)) } returns Unit

        // When
        useCase(keys)

        // Then
        assertEquals(keys, slot.captured)
        assertEquals(2, slot.captured.size)
        assertTrue(slot.captured.contains(key1))
        assertTrue(slot.captured.contains(key2))
    }

    // ==================== Empty List ====================

    @Test
    fun `invoke with empty list calls repository with empty list`() = runTest {
        // Given
        val keys = emptyList<Preferences.Key<*>>()
        coEvery { dataStoreManager.removePreferences(any()) } returns Unit

        // When
        useCase(keys)

        // Then
        coVerify(exactly = 1) { dataStoreManager.removePreferences(emptyList()) }
    }

    // ==================== Single Key ====================

    @Test
    fun `invoke with single key calls repository correctly`() = runTest {
        // Given
        val key = stringPreferencesKey("singleKey")
        val keys = listOf(key)
        coEvery { dataStoreManager.removePreferences(any()) } returns Unit

        // When
        useCase(keys)

        // Then
        coVerify(exactly = 1) { dataStoreManager.removePreferences(listOf(key)) }
    }

    // ==================== Multiple Keys ====================

    @Test
    fun `invoke with multiple keys calls repository correctly`() = runTest {
        // Given
        val keys = listOf(
            stringPreferencesKey("key1"),
            intPreferencesKey("key2"),
            booleanPreferencesKey("key3")
        )
        coEvery { dataStoreManager.removePreferences(any()) } returns Unit

        // When
        useCase(keys)

        // Then
        coVerify(exactly = 1) { dataStoreManager.removePreferences(keys) }
    }

    // ==================== Different Key Types ====================

    @Test
    fun `invoke with string preference key works`() = runTest {
        // Given
        val keys = listOf(stringPreferencesKey("stringKey"))
        coEvery { dataStoreManager.removePreferences(any()) } returns Unit

        // When
        useCase(keys)

        // Then
        coVerify(exactly = 1) { dataStoreManager.removePreferences(keys) }
    }

    @Test
    fun `invoke with int preference key works`() = runTest {
        // Given
        val keys = listOf(intPreferencesKey("intKey"))
        coEvery { dataStoreManager.removePreferences(any()) } returns Unit

        // When
        useCase(keys)

        // Then
        coVerify(exactly = 1) { dataStoreManager.removePreferences(keys) }
    }

    @Test
    fun `invoke with boolean preference key works`() = runTest {
        // Given
        val keys = listOf(booleanPreferencesKey("boolKey"))
        coEvery { dataStoreManager.removePreferences(any()) } returns Unit

        // When
        useCase(keys)

        // Then
        coVerify(exactly = 1) { dataStoreManager.removePreferences(keys) }
    }

    @Test
    fun `invoke with mixed key types works`() = runTest {
        // Given
        val keys: List<Preferences.Key<*>> = listOf(
            stringPreferencesKey("string"),
            intPreferencesKey("int"),
            booleanPreferencesKey("bool")
        )
        coEvery { dataStoreManager.removePreferences(any()) } returns Unit

        // When
        useCase(keys)

        // Then
        coVerify(exactly = 1) { dataStoreManager.removePreferences(keys) }
    }

    // ==================== Exception Propagation ====================

    @Test(expected = RuntimeException::class)
    fun `invoke propagates exception from repository`() = runTest {
        // Given
        val keys = listOf(stringPreferencesKey("key"))
        coEvery { dataStoreManager.removePreferences(any()) } throws RuntimeException("DataStore error")

        // When
        useCase(keys)

        // Then - exception is thrown
    }

    @Test(expected = IllegalStateException::class)
    fun `invoke propagates IllegalStateException from repository`() = runTest {
        // Given
        val keys = listOf(stringPreferencesKey("key"))
        coEvery { dataStoreManager.removePreferences(any()) } throws IllegalStateException("Invalid state")

        // When
        useCase(keys)

        // Then - exception is thrown
    }

    // ==================== Interaction Verification ====================

    @Test
    fun `invoke calls repository exactly once`() = runTest {
        // Given
        val keys = listOf(stringPreferencesKey("key"))
        coEvery { dataStoreManager.removePreferences(any()) } returns Unit

        // When
        useCase(keys)
        useCase(keys)

        // Then
        coVerify(exactly = 2) { dataStoreManager.removePreferences(keys) }
    }
}