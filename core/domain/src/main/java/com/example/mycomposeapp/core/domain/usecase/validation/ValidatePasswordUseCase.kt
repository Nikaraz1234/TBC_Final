package com.example.mycomposeapp.core.domain.usecase.validation

import javax.inject.Inject

class ValidatePasswordUseCase @Inject constructor() {

    operator fun invoke(password: String): ValidationResult {
        if (password.isBlank()) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Password cannot be empty"
            )
        }
        if (password.length < 8) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Password must be at least 8 characters"
            )
        }
        if (!password.any { it.isUpperCase() }) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Password must contain at least one uppercase letter"
            )
        }
        if (!password.any { it.isLowerCase() }) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Password must contain at least one lowercase letter"
            )
        }
        if (!password.any { it.isDigit() }) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Password must contain at least one number"
            )
        }
        return ValidationResult(isValid = true)
    }
}
