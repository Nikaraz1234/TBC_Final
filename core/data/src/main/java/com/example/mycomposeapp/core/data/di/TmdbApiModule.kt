package com.example.mycomposeapp.core.data.di

import com.example.mycomposeapp.core.data.BuildConfig
import com.example.mycomposeapp.core.data.common.applyDefaultTimeouts
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TmdbApiModule {

    @Provides
    @Singleton
    @TmdbOkHttpClient
    fun provideTmdbOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val url = original.url.newBuilder()
                .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                .build()
            chain.proceed(original.newBuilder().url(url).build())
        }
        .addInterceptor(loggingInterceptor)
        .applyDefaultTimeouts()
        .build()

    @Provides
    @Singleton
    @TmdbRetrofit
    fun provideTmdbRetrofit(
        @TmdbOkHttpClient okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.TMDB_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }
}
