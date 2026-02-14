package com.example.mycomposeapp.feature.game.data.remote.games.service

import com.example.mycomposeapp.feature.game.data.remote.games.dto.GameScreenshotDto
import retrofit2.http.Body
import retrofit2.http.POST

interface GamesService {
    @POST("games")
    suspend fun searchGames(
        @Body query: okhttp3.RequestBody
    ): List<GameScreenshotDto>
}