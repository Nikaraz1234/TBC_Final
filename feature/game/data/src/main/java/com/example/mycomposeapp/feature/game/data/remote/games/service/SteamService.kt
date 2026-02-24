package com.example.mycomposeapp.feature.game.data.remote.games.service

import com.example.mycomposeapp.feature.game.data.remote.games.dto.SteamAchievementPercentageResponse
import com.example.mycomposeapp.feature.game.data.remote.games.dto.SteamGameSchemaResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SteamService {

    @GET("ISteamUserStats/GetSchemaForGame/v2/")
    suspend fun getGameSchema(
        @Query("key") key: String,
        @Query("appid") appId: Int,
        @Query("l") language: String = "english"
    ): SteamGameSchemaResponse

    @GET("ISteamUserStats/GetGlobalAchievementPercentagesForApp/v2/")
    suspend fun getAchievementPercentages(
        @Query("gameid") gameId: Int
    ): SteamAchievementPercentageResponse
}
