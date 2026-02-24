package com.example.mycomposeapp.feature.game.data.remote.games.service

import com.example.mycomposeapp.feature.game.data.remote.games.dto.SteamSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SteamStoreService {

    @GET("search/results/")
    suspend fun searchGames(
        @Query("json") json: Int = 1,
        @Query("category2") category: Int = 22,
        @Query("filter") filter: String = "topsellers",
        @Query("start") start: Int,
        @Query("count") count: Int
    ): SteamSearchResponse

    @GET("search/results/")
    suspend fun searchGamesByName(
        @Query("term") term: String,
        @Query("json") json: Int = 1,
        @Query("start") start: Int = 0,
        @Query("count") count: Int = 10
    ): SteamSearchResponse
}
