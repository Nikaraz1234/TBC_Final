package com.example.mycomposeapp.feature.game.domain.repository.comics

import com.example.mycomposeapp.feature.game.domain.model.comics.RankleManga

interface RankleRepository {
    suspend fun getRankleManga(excludeIDs: Set<Long>): RankleManga
}
