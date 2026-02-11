package com.example.mycomposeapp.core.domain.usecase.validation

import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor() {

    private val emailPattern = Regex(
        "[a-zA-Z0-9+._%\\-]{1,256}" +
        "@" +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
        "(" +
        "\\." +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
        ")+"
    )

    operator fun invoke(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Email cannot be empty"
            )
        }
        if (!emailPattern.matches(email)) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Please enter a valid email address"
            )
        }
        return ValidationResult(isValid = true)
    }
}
