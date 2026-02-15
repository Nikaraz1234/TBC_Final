package com.example.mycomposeapp.feature.game.data.remote.comics

import com.example.mycomposeapp.feature.game.data.remote.comics.dto.MalMangaRankingResponse
import com.example.mycomposeapp.feature.game.data.remote.comics.dto.MalMangaSearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MalApiService {
    @GET("manga/ranking")
    suspend fun getMangaRanking(
        @Query("ranking_type") rankingType: String = "all",
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("fields") fields: String = "id,title,main_picture,mean"
    ): MalMangaRankingResponse

    @GET("manga/{id}")
    suspend fun getMangaDetails(
        @Path("id") id: Int,
        @Query("fields") fields: String =
            "id,title,synopsis,genres,start_date,authors,mean"
    ): MalMangaRankingResponse
    @GET("manga")
    suspend fun searchManga(
        @Query("q") query: String,
        @Query("limit") limit: Int = 8,
        @Query("fields") fields: String = "id,title,main_picture,mean"
    ): MalMangaSearchResponse
}