package com.example.mycomposeapp.feature.leaderboard.presentation

import com.example.mycomposeapp.core.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(

) : BaseViewModel<
        LeaderboardContract.State,
        LeaderboardContract.SideEffect,
        LeaderboardContract.Event
        >(
    initialState = LeaderboardContract.State()
){

    fun onEvent(event: LeaderboardContract.Event){
        when(event){
            is LeaderboardContract.Event.CategoryChanged -> categoryChanged(event.category)
            LeaderboardContract.Event.LoadLeaderboard -> TODO()
            is LeaderboardContract.Event.ModeChanged -> modeChanged(event.mode)
        }
    }

    private fun categoryChanged(category: String){
        setState { copy(selectedCategory = category) }
    }

    private fun modeChanged(mode: String){
        setState { copy(selectedMode = mode) }
    }


}