package com.example.mycomposeapp.feature.game.domain.model

data class MangaItem(
    val id: Long,
    val title: String,
    val imageUrl: String,
    val rating: Double
)

data class MangaPair(
    val mangaA: MangaItem,
    val mangaB: MangaItem
)
