package com.example.mycomposeapp.core.domain.usecase.validation

import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ValidateEmailUseCaseTest {

    private lateinit var useCase: ValidateEmailUseCase

    @Before
    fun setUp() {
        useCase = ValidateEmailUseCase()
    }

    @Test
    fun `empty email returns invalid with empty error message`() {
        val result = useCase("")

        assertFalse(result.isValid)
        assertEquals("Email cannot be empty", result.errorMessage)
    }

    @Test
    fun `blank email with spaces returns invalid with empty error message`() {
        val result = useCase("   ")

        assertFalse(result.isValid)
        assertEquals("Email cannot be empty", result.errorMessage)
    }

    @Test
    fun `blank email with tabs returns invalid with empty error message`() {
        val result = useCase("\t\t")

        assertFalse(result.isValid)
        assertEquals("Email cannot be empty", result.errorMessage)
    }

    @Test
    fun `blank email with newlines returns invalid with empty error message`() {
        val result = useCase("\n\n")

        assertFalse(result.isValid)
        assertEquals("Email cannot be empty", result.errorMessage)
    }

    // ==================== Invalid Format Cases ====================

    @Test
    fun `email without @ symbol returns invalid`() {
        val result = useCase("testexample.com")

        assertFalse(result.isValid)
        assertEquals("Please enter a valid email address", result.errorMessage)
    }

    @Test
    fun `email without domain returns invalid`() {
        val result = useCase("test@")

        assertFalse(result.isValid)
        assertEquals("Please enter a valid email address", result.errorMessage)
    }

    @Test
    fun `email without local part returns invalid`() {
        val result = useCase("@example.com")

        assertFalse(result.isValid)
        assertEquals("Please enter a valid email address", result.errorMessage)
    }

    @Test
    fun `email without TLD returns invalid`() {
        val result = useCase("test@example")

        assertFalse(result.isValid)
        assertEquals("Please enter a valid email address", result.errorMessage)
    }

    @Test
    fun `email with double @ returns invalid`() {
        val result = useCase("test@@example.com")

        assertFalse(result.isValid)
        assertEquals("Please enter a valid email address", result.errorMessage)
    }

    @Test
    fun `email with domain starting with dot returns invalid`() {
        val result = useCase("test@.example.com")

        assertFalse(result.isValid)
        assertEquals("Please enter a valid email address", result.errorMessage)
    }

    @Test
    fun `email with domain starting with hyphen returns invalid`() {
        val result = useCase("test@-example.com")

        assertFalse(result.isValid)
        assertEquals("Please enter a valid email address", result.errorMessage)
    }

    @Test
    fun `email with spaces returns invalid`() {
        val result = useCase("test @example.com")

        assertFalse(result.isValid)
        assertEquals("Please enter a valid email address", result.errorMessage)
    }

    @Test
    fun `email with only @ symbol returns invalid`() {
        val result = useCase("@")

        assertFalse(result.isValid)
        assertEquals("Please enter a valid email address", result.errorMessage)
    }

    @Test
    fun `email with TLD starting with dot returns invalid`() {
        val result = useCase("test@example..com")

        assertFalse(result.isValid)
        assertEquals("Please enter a valid email address", result.errorMessage)
    }

    // ==================== Valid Format Cases ====================

    @Test
    fun `simple valid email returns valid`() {
        val result = useCase("test@example.com")

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `email with subdomain returns valid`() {
        val result = useCase("test@mail.example.com")

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `email with multiple subdomains returns valid`() {
        val result = useCase("test@a.b.c.example.com")

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `email with plus sign returns valid`() {
        val result = useCase("test+tag@example.com")

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `email with dots in local part returns valid`() {
        val result = useCase("first.last@example.com")

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `email with underscore returns valid`() {
        val result = useCase("test_user@example.com")

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `email with hyphen in local part returns valid`() {
        val result = useCase("test-user@example.com")

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `email with percent sign returns valid`() {
        val result = useCase("test%user@example.com")

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `email with numbers in local part returns valid`() {
        val result = useCase("test123@example.com")

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `email with numbers in domain returns valid`() {
        val result = useCase("test@example123.com")

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `email with hyphen in domain returns valid`() {
        val result = useCase("test@my-example.com")

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `email with two letter TLD returns valid`() {
        val result = useCase("test@example.co")

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `email with country code TLD returns valid`() {
        val result = useCase("test@example.co.uk")

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `email with long TLD returns valid`() {
        val result = useCase("test@example.technology")

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `email with uppercase letters returns valid`() {
        val result = useCase("Test@Example.COM")

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `email with single character local part returns valid`() {
        val result = useCase("a@example.com")

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `email with single character domain parts returns valid`() {
        val result = useCase("test@a.co")

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `email with all special characters in local part returns valid`() {
        val result = useCase("a+b.c_d%e-f@example.com")

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }
}