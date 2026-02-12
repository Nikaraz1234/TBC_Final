package com.example.mycomposeapp.feature.game.domain.repository

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.feature.game.domain.model.GameConfig
import com.example.mycomposeapp.feature.game.domain.model.Question
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    fun getQuestions(config: GameConfig): Flow<Resource<List<Question>>>
}
