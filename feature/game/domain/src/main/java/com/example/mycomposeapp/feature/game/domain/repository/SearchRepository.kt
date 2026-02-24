    package com.example.mycomposeapp.feature.game.domain.repository

    import com.example.mycomposeapp.core.domain.common.Resource
    import com.example.mycomposeapp.feature.game.domain.model.SearchResult
    import kotlinx.coroutines.flow.Flow

    interface SearchRepository {
        fun search(query: String): Flow<Resource<List<SearchResult>>>
    }
