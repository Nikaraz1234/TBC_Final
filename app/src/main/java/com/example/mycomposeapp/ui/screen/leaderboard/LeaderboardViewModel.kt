package com.example.mycomposeapp.ui.screen.leaderboard

import com.example.mycomposeapp.ui.common.BaseViewModel
import com.example.mycomposeapp.ui.screen.splash.SplashContract
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
    }
}