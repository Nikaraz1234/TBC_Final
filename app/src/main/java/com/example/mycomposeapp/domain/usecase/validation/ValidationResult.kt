package com.example.mycomposeapp.domain.usecase.validation

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)
