package com.example.mycomposeapp.feature.main.presentation.model

import androidx.annotation.DrawableRes

data class GameMode(
    val id: String,
    val name: String,
    val description: String,
    @DrawableRes val iconRes: Int,
    val isAvailable: Boolean = true
)
