package com.example.mycomposeapp.feature.game.data.remote.comics

import com.example.mycomposeapp.feature.game.data.remote.comics.dto.MalMangaRankingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MalApiService {
    @GET("manga/ranking")
    suspend fun getMangaRanking(
        @Query("ranking_type") rankingType: String = "all",
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("fields") fields: String = "id,title,main_picture,mean"
    ): MalMangaRankingResponse
}