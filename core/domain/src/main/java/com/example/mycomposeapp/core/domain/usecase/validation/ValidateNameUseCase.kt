package com.example.mycomposeapp.core.domain.usecase.validation

import com.example.mycomposeapp.core.domain.model.ValidationConstants
import javax.inject.Inject

class ValidateNameUseCase @Inject constructor() {

    operator fun invoke(name: String): ValidationResult {
        if (name.isBlank()) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Name cannot be empty"
            )
        }
        if (name.length < ValidationConstants.NAME_MIN_LENGTH) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Name must be at least ${ValidationConstants.NAME_MIN_LENGTH} characters"
            )
        }
        if (name.length > ValidationConstants.NAME_MAX_LENGTH) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Name cannot exceed ${ValidationConstants.NAME_MAX_LENGTH} characters"
            )
        }
        return ValidationResult(isValid = true)
    }
}
