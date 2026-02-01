package com.example.mycomposeapp.domain.usecase.validation

import android.util.Patterns
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor() {

    operator fun invoke(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Email cannot be empty"
            )
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Please enter a valid email address"
            )
        }
        return ValidationResult(isValid = true)
    }
}
