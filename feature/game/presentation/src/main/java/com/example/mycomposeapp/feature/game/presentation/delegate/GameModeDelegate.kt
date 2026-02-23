package com.example.mycomposeapp.feature.game.presentation.delegate

import com.example.mycomposeapp.feature.game.domain.model.AnswerResult

interface GameModeDelegate {
    fun attach(scope: DelegateScope)
    fun loadGame()
    fun onAnswerSubmitted(answer: String)
    fun onNextQuestion()
    fun onRetryGame()
    fun onExitGame()
    fun onRevealMore() {}
    fun onUseHint() {}
    fun onUseCategoryHint() {}
    fun getAnswerResults(): List<AnswerResult>
    fun onCleared() {}
}
