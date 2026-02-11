package com.example.mycomposeapp.core.domain.usecase.validation

import javax.inject.Inject

class ValidateNameUseCase @Inject constructor() {

    operator fun invoke(name: String): ValidationResult {
        if (name.isBlank()) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Name cannot be empty"
            )
        }
        if (name.length < 2) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Name must be at least 2 characters"
            )
        }
        if (name.length > 50) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Name cannot exceed 50 characters"
            )
        }
        return ValidationResult(isValid = true)
    }
}
