package com.example.mycomposeapp.core.domain.usecase.validation

import com.example.mycomposeapp.core.domain.model.ValidationResult

import com.example.mycomposeapp.core.domain.constants.ValidationConstants
import javax.inject.Inject

class ValidatePasswordUseCase @Inject constructor() {

    operator fun invoke(password: String): ValidationResult {
        if (password.isBlank()) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Password cannot be empty"
            )
        }
        if (password.length < ValidationConstants.PASSWORD_MIN_LENGTH) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Password must be at least ${ValidationConstants.PASSWORD_MIN_LENGTH} characters"
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
