package com.example.mycomposeapp.feature.game.domain.usecase

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.feature.game.domain.model.GameConfig
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQuestionsUseCase @Inject constructor(
    private val questionRepositories: Map<String, @JvmSuppressWildcards QuestionRepository>
) {
    operator fun invoke(config: GameConfig): Flow<Resource<List<Question>>> {
        val repo = questionRepositories[config.categoryType]
            ?: throw IllegalArgumentException("No question repository for category: ${config.categoryType}")
        return repo.getQuestions(config)
    }
}
