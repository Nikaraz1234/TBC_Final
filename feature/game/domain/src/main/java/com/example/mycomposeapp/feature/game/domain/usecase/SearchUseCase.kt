package com.example.mycomposeapp.feature.game.domain.usecase

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.feature.game.domain.model.SearchResult
import com.example.mycomposeapp.feature.game.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val searchRepositories: Map<String, @JvmSuppressWildcards SearchRepository>
) {
    operator fun invoke(
        categoryType: String,
        query: String,
        gameModeId: String? = null
    ): Flow<Resource<List<SearchResult>>> {
        val compositeKey = gameModeId?.let { "${categoryType}_$it" }
        val repo = (compositeKey?.let { searchRepositories[it] })
            ?: searchRepositories[categoryType]
            ?: throw IllegalArgumentException("No search repository for category: $categoryType")
        return repo.search(query)
    }
}
