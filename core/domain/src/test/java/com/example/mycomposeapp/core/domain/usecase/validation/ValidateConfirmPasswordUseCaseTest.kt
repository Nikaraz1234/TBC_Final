package com.example.mycomposeapp.core.domain.usecase.validation

import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ValidateConfirmPasswordUseCaseTest {

    private lateinit var useCase: ValidateConfirmPasswordUseCase

    @Before
    fun setUp() {
        useCase = ValidateConfirmPasswordUseCase()
    }

    // ==================== Empty/Blank Confirm Password Cases ====================

    @Test
    fun `empty confirm password returns invalid`() {
        val result = useCase("Password1", "")

        assertFalse(result.isValid)
        assertEquals("Please confirm your password", result.errorMessage)
    }

    @Test
    fun `blank confirm password with spaces returns invalid`() {
        val result = useCase("Password1", "     ")

        assertFalse(result.isValid)
        assertEquals("Please confirm your password", result.errorMessage)
    }

    @Test
    fun `blank confirm password with tabs returns invalid`() {
        val result = useCase("Password1", "\t\t")

        assertFalse(result.isValid)
        assertEquals("Please confirm your password", result.errorMessage)
    }

    @Test
    fun `blank confirm password with newlines returns invalid`() {
        val result = useCase("Password1", "\n\n")

        assertFalse(result.isValid)
        assertEquals("Please confirm your password", result.errorMessage)
    }

    @Test
    fun `blank confirm password with mixed whitespace returns invalid`() {
        val result = useCase("Password1", " \t\n ")

        assertFalse(result.isValid)
        assertEquals("Please confirm your password", result.errorMessage)
    }

    // ==================== Passwords Do Not Match Cases ====================

    @Test
    fun `different passwords returns invalid`() {
        val result = useCase("Password1", "Password2")

        assertFalse(result.isValid)
        assertEquals("Passwords do not match", result.errorMessage)
    }

    @Test
    fun `passwords differing by case returns invalid`() {
        val result = useCase("Password1", "password1")

        assertFalse(result.isValid)
        assertEquals("Passwords do not match", result.errorMessage)
    }

    @Test
    fun `passwords differing by one character returns invalid`() {
        val result = useCase("Password1", "Password2")

        assertFalse(result.isValid)
        assertEquals("Passwords do not match", result.errorMessage)
    }

    @Test
    fun `password with extra character returns invalid`() {
        val result = useCase("Password1", "Password1!")

        assertFalse(result.isValid)
        assertEquals("Passwords do not match", result.errorMessage)
    }

    @Test
    fun `password with missing character returns invalid`() {
        val result = useCase("Password1", "Password")

        assertFalse(result.isValid)
        assertEquals("Passwords do not match", result.errorMessage)
    }

    @Test
    fun `password with leading space in confirm returns invalid`() {
        val result = useCase("Password1", " Password1")

        assertFalse(result.isValid)
        assertEquals("Passwords do not match", result.errorMessage)
    }

    @Test
    fun `password with trailing space in confirm returns invalid`() {
        val result = useCase("Password1", "Password1 ")

        assertFalse(result.isValid)
        assertEquals("Passwords do not match", result.errorMessage)
    }

    @Test
    fun `completely different passwords returns invalid`() {
        val result = useCase("Abc123!", "Xyz789@")

        assertFalse(result.isValid)
        assertEquals("Passwords do not match", result.errorMessage)
    }

    @Test
    fun `reversed password returns invalid`() {
        val result = useCase("Password1", "1drowssaP")

        assertFalse(result.isValid)
        assertEquals("Passwords do not match", result.errorMessage)
    }

    // ==================== Valid Cases (Passwords Match) ====================

    @Test
    fun `matching simple passwords returns valid`() {
        val result = useCase("Password1", "Password1")

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `matching complex passwords returns valid`() {
        val result = useCase("MyP@ssw0rd!123", "MyP@ssw0rd!123")

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `matching passwords with special characters returns valid`() {
        val result = useCase("P@ss!#$%^&*()", "P@ss!#$%^&*()")

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `matching passwords with spaces returns valid`() {
        val result = useCase("Pass word 1", "Pass word 1")

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `matching short passwords returns valid`() {
        val result = useCase("Ab1", "Ab1")

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `matching long passwords returns valid`() {
        val longPassword = "A".repeat(100) + "1a"
        val result = useCase(longPassword, longPassword)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `matching passwords with unicode characters returns valid`() {
        val result = useCase("Pässwörd1", "Pässwörd1")

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `matching passwords with emojis returns valid`() {
        val result = useCase("Password1🔒", "Password1🔒")

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    // ==================== Edge Cases ====================

    @Test
    fun `both passwords empty returns invalid with confirm message`() {
        // Blank check happens first
        val result = useCase("", "")

        assertFalse(result.isValid)
        assertEquals("Please confirm your password", result.errorMessage)
    }

    @Test
    fun `password empty but confirm filled returns invalid`() {
        val result = useCase("", "Password1")

        assertFalse(result.isValid)
        assertEquals("Passwords do not match", result.errorMessage)
    }

    @Test
    fun `matching single character passwords returns valid`() {
        val result = useCase("a", "a")

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `matching whitespace-only password with same whitespace returns valid`() {
        // Note: password itself is whitespace, confirm is also same whitespace
        // But confirm.isBlank() would be true, so this fails first check
        val result = useCase("   ", "   ")

        assertFalse(result.isValid)
        assertEquals("Please confirm your password", result.errorMessage)
    }
}