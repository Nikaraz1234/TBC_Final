package com.example.mycomposeapp.feature.game.presentation.delegate

import com.example.mycomposeapp.feature.game.presentation.GameContract
import kotlinx.coroutines.CoroutineScope

interface DelegateScope {
    val coroutineScope: CoroutineScope
    fun currentState(): GameContract.State
    fun updateState(reducer: GameContract.State.() -> GameContract.State)
    fun emitSideEffect(effect: GameContract.SideEffect)
}
