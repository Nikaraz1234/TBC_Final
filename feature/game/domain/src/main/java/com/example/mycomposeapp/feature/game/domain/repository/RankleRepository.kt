package com.example.mycomposeapp.feature.game.domain.repository

import com.example.mycomposeapp.feature.game.domain.model.game.RankleManga

interface RankleRepository {
    suspend fun getRankleManga(excludeIDs: Set<Long>): RankleManga
}