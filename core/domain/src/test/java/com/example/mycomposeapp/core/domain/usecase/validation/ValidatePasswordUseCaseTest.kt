package com.example.mycomposeapp.core.domain.usecase.validation

import com.example.mycomposeapp.core.domain.constants.ValidationConstants
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ValidatePasswordUseCaseTest {

    private lateinit var useCase: ValidatePasswordUseCase

    @Before
    fun setUp() {
        useCase = ValidatePasswordUseCase()
    }

    // ==================== Empty/Blank Cases ====================

    @Test
    fun `empty password returns invalid with empty error message`() {
        val result = useCase("")

        assertFalse(result.isValid)
        assertEquals("Password cannot be empty", result.errorMessage)
    }

    @Test
    fun `blank password with spaces returns invalid with empty error message`() {
        val result = useCase("     ")

        assertFalse(result.isValid)
        assertEquals("Password cannot be empty", result.errorMessage)
    }

    @Test
    fun `blank password with tabs returns invalid with empty error message`() {
        val result = useCase("\t\t\t")

        assertFalse(result.isValid)
        assertEquals("Password cannot be empty", result.errorMessage)
    }

    @Test
    fun `blank password with newlines returns invalid with empty error message`() {
        val result = useCase("\n\n")

        assertFalse(result.isValid)
        assertEquals("Password cannot be empty", result.errorMessage)
    }

    // ==================== Length Cases ====================

    @Test
    fun `password shorter than minimum length returns invalid`() {
        // Has uppercase, lowercase, digit but too short
        val shortPassword = "Abc1"

        val result = useCase(shortPassword)

        assertFalse(result.isValid)
        assertEquals(
            "Password must be at least ${ValidationConstants.PASSWORD_MIN_LENGTH} characters",
            result.errorMessage
        )
    }

    @Test
    fun `password one character below minimum length returns invalid`() {
        // Build password that's exactly MIN_LENGTH - 1
        val password = buildString {
            append("Aa1")
            repeat(ValidationConstants.PASSWORD_MIN_LENGTH - 4) { append("x") }
        }

        val result = useCase(password)

        assertFalse(result.isValid)
        assertEquals(
            "Password must be at least ${ValidationConstants.PASSWORD_MIN_LENGTH} characters",
            result.errorMessage
        )
    }

    // ==================== Uppercase Cases ====================

    @Test
    fun `password without uppercase letter returns invalid`() {
        // Long enough, has lowercase and digit, but no uppercase
        val password = "abcdefgh1"

        val result = useCase(password)

        assertFalse(result.isValid)
        assertEquals("Password must contain at least one uppercase letter", result.errorMessage)
    }

    @Test
    fun `password with only digits and lowercase returns invalid for uppercase`() {
        val password = "password123"

        val result = useCase(password)

        assertFalse(result.isValid)
        assertEquals("Password must contain at least one uppercase letter", result.errorMessage)
    }

    // ==================== Lowercase Cases ====================

    @Test
    fun `password without lowercase letter returns invalid`() {
        // Long enough, has uppercase and digit, but no lowercase
        val password = "ABCDEFGH1"

        val result = useCase(password)

        assertFalse(result.isValid)
        assertEquals("Password must contain at least one lowercase letter", result.errorMessage)
    }

    @Test
    fun `password with only digits and uppercase returns invalid for lowercase`() {
        val password = "PASSWORD123"

        val result = useCase(password)

        assertFalse(result.isValid)
        assertEquals("Password must contain at least one lowercase letter", result.errorMessage)
    }

    // ==================== Digit Cases ====================

    @Test
    fun `password without digit returns invalid`() {
        // Long enough, has uppercase and lowercase, but no digit
        val password = "Abcdefghi"

        val result = useCase(password)

        assertFalse(result.isValid)
        assertEquals("Password must contain at least one number", result.errorMessage)
    }

    @Test
    fun `password with only letters returns invalid for digit`() {
        val password = "AbcDefGhi"

        val result = useCase(password)

        assertFalse(result.isValid)
        assertEquals("Password must contain at least one number", result.errorMessage)
    }

    // ==================== Valid Cases ====================

    @Test
    fun `password meeting all requirements returns valid`() {
        val password = "Abcdefg1"

        val result = useCase(password)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `password exactly at minimum length with all requirements returns valid`() {
        // Build password that's exactly MIN_LENGTH
        val password = buildString {
            append("Aa1")
            repeat(ValidationConstants.PASSWORD_MIN_LENGTH - 3) { append("x") }
        }

        val result = useCase(password)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `password longer than minimum with all requirements returns valid`() {
        val password = "Abcdefghijklmnop1"

        val result = useCase(password)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `password with multiple uppercase letters returns valid`() {
        val password = "ABCdefgh1"

        val result = useCase(password)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `password with multiple digits returns valid`() {
        val password = "Abcdef123"

        val result = useCase(password)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `password with special characters returns valid`() {
        val password = "Abcdef1!"

        val result = useCase(password)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `password with digit at start returns valid`() {
        val password = "1Abcdefgh"

        val result = useCase(password)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `password with uppercase at end returns valid`() {
        val password = "abcdefg1H"

        val result = useCase(password)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `complex valid password returns valid`() {
        val password = "MyP@ssw0rd!123"

        val result = useCase(password)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    // ==================== Edge Cases ====================

    @Test
    fun `password with single uppercase among many lowercase returns valid`() {
        val password = "abcdefgH1"

        val result = useCase(password)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `password with single lowercase among many uppercase returns valid`() {
        val password = "ABCDEFGh1"

        val result = useCase(password)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `password with single digit among many letters returns valid`() {
        val password = "Abcdefgh1"

        val result = useCase(password)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }
}