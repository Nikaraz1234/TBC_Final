package com.example.mycomposeapp.feature.achievements.data.dto

import com.google.firebase.firestore.DocumentId

data class AchievementDto(
    @DocumentId val id: String = "",
    val name: String = "",
    val description: String = "",
    val icon: String = "",
    val category: String = "GENERAL",
    val xpReward: Int = 0,
    val conditionType: String = "GAMES_PLAYED",
    val conditionValue: Int = 0,
    val conditionKey: String? = null
)
