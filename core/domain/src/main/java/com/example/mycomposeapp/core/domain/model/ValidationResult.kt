package com.example.mycomposeapp.core.domain.model

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)
