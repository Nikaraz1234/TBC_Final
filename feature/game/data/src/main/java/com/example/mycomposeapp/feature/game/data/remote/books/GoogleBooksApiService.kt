package com.example.mycomposeapp.feature.game.data.remote.books

import com.example.mycomposeapp.feature.game.data.remote.books.dto.GoogleBooksResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApiService {
    @GET("volumes")
    suspend fun searchVolumes(
        @Query("q") query: String,
        @Query("langRestrict") langRestrict: String = "en",
        @Query("orderBy") orderBy: String = "relevance",
        @Query("maxResults") maxResults: Int = 40,
        @Query("startIndex") startIndex: Int = 0,
        @Query("printType") printType: String = "books"
    ): GoogleBooksResponseDto
}
