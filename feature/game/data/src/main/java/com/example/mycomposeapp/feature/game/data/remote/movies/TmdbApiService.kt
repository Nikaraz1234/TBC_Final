package com.example.mycomposeapp.feature.game.data.remote.movies

import com.example.mycomposeapp.feature.game.data.remote.movies.dto.TmdbMovieDetailResponse
import com.example.mycomposeapp.feature.game.data.remote.movies.dto.TmdbMovieListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {

    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("page") page: Int = 1): TmdbMovieListResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(@Query("page") page: Int = 1): TmdbMovieListResponse

    @GET("search/movie")
    suspend fun searchMovies(@Query("query") query: String): TmdbMovieListResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(@Path("movie_id") movieId: Int): TmdbMovieDetailResponse
}
