package com.example.mycomposeapp.core.domain.usecase.validation

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)
