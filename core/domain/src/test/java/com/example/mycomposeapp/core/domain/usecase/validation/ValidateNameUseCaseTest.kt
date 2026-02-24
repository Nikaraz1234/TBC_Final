package com.example.mycomposeapp.core.domain.usecase.validation

import com.example.mycomposeapp.core.domain.constants.ValidationConstants
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ValidateNameUseCaseTest {

    private lateinit var useCase: ValidateNameUseCase

    @Before
    fun setUp() {
        useCase = ValidateNameUseCase()
    }

    // ==================== Empty/Blank Cases ====================

    @Test
    fun `empty name returns invalid with empty error message`() {
        val result = useCase("")

        assertFalse(result.isValid)
        assertEquals("Name cannot be empty", result.errorMessage)
    }

    @Test
    fun `blank name with spaces returns invalid with empty error message`() {
        val result = useCase("     ")

        assertFalse(result.isValid)
        assertEquals("Name cannot be empty", result.errorMessage)
    }

    @Test
    fun `blank name with tabs returns invalid with empty error message`() {
        val result = useCase("\t\t")

        assertFalse(result.isValid)
        assertEquals("Name cannot be empty", result.errorMessage)
    }

    @Test
    fun `blank name with newlines returns invalid with empty error message`() {
        val result = useCase("\n\n")

        assertFalse(result.isValid)
        assertEquals("Name cannot be empty", result.errorMessage)
    }

    // ==================== Min Length Cases ====================

    @Test
    fun `name shorter than minimum length returns invalid`() {
        val shortName = "A".repeat(ValidationConstants.NAME_MIN_LENGTH - 1)

        val result = useCase(shortName)

        assertFalse(result.isValid)
        assertEquals(
            "Name must be at least ${ValidationConstants.NAME_MIN_LENGTH} characters",
            result.errorMessage
        )
    }

    @Test
    fun `name one character below minimum returns invalid`() {
        val name = "x".repeat(ValidationConstants.NAME_MIN_LENGTH - 1)

        val result = useCase(name)

        assertFalse(result.isValid)
        assertEquals(
            "Name must be at least ${ValidationConstants.NAME_MIN_LENGTH} characters",
            result.errorMessage
        )
    }

    // ==================== Max Length Cases ====================

    @Test
    fun `name exceeding maximum length returns invalid`() {
        val longName = "A".repeat(ValidationConstants.NAME_MAX_LENGTH + 1)

        val result = useCase(longName)

        assertFalse(result.isValid)
        assertEquals(
            "Name cannot exceed ${ValidationConstants.NAME_MAX_LENGTH} characters",
            result.errorMessage
        )
    }

    @Test
    fun `name one character above maximum returns invalid`() {
        val name = "x".repeat(ValidationConstants.NAME_MAX_LENGTH + 1)

        val result = useCase(name)

        assertFalse(result.isValid)
        assertEquals(
            "Name cannot exceed ${ValidationConstants.NAME_MAX_LENGTH} characters",
            result.errorMessage
        )
    }

    @Test
    fun `name significantly exceeding maximum returns invalid`() {
        val veryLongName = "A".repeat(ValidationConstants.NAME_MAX_LENGTH + 100)

        val result = useCase(veryLongName)

        assertFalse(result.isValid)
        assertEquals(
            "Name cannot exceed ${ValidationConstants.NAME_MAX_LENGTH} characters",
            result.errorMessage
        )
    }

    // ==================== Valid Cases ====================

    @Test
    fun `name exactly at minimum length returns valid`() {
        val name = "A".repeat(ValidationConstants.NAME_MIN_LENGTH)

        val result = useCase(name)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `name exactly at maximum length returns valid`() {
        val name = "A".repeat(ValidationConstants.NAME_MAX_LENGTH)

        val result = useCase(name)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `name between min and max length returns valid`() {
        val midLength = (ValidationConstants.NAME_MIN_LENGTH + ValidationConstants.NAME_MAX_LENGTH) / 2
        val name = "A".repeat(midLength)

        val result = useCase(name)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `simple name returns valid`() {
        val name = "John"

        val result = useCase(name)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `name with spaces returns valid`() {
        val name = "John Doe"

        val result = useCase(name)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `name with multiple spaces returns valid`() {
        val name = "John Michael Doe"

        val result = useCase(name)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `name with hyphen returns valid`() {
        val name = "Mary-Jane"

        val result = useCase(name)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `name with apostrophe returns valid`() {
        val name = "O'Connor"

        val result = useCase(name)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `name with numbers returns valid`() {
        val name = "John3"

        val result = useCase(name)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `name with uppercase letters returns valid`() {
        val name = "JOHN"

        val result = useCase(name)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `name with lowercase letters returns valid`() {
        val name = "john"

        val result = useCase(name)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `name with mixed case returns valid`() {
        val name = "JoHn DoE"

        val result = useCase(name)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `name with unicode characters returns valid`() {
        val name = "José García"

        val result = useCase(name)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `name with special characters returns valid`() {
        val name = "John@Doe"

        val result = useCase(name)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    // ==================== Edge Cases ====================

    @Test
    fun `name one character above minimum returns valid`() {
        val name = "A".repeat(ValidationConstants.NAME_MIN_LENGTH + 1)

        val result = useCase(name)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `name one character below maximum returns valid`() {
        val name = "A".repeat(ValidationConstants.NAME_MAX_LENGTH - 1)

        val result = useCase(name)

        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }
}