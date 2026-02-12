package com.example.mycomposeapp.feature.game.domain.usecase

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.feature.game.domain.model.GameConfig
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQuestionsUseCase @Inject constructor(
    private val questionRepository: QuestionRepository
) {
    operator fun invoke(config: GameConfig): Flow<Resource<List<Question>>> {
        return questionRepository.getQuestions(config)
    }
}
