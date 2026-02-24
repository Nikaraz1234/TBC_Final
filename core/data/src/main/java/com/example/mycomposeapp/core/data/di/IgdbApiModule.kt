package com.example.mycomposeapp.core.data.di

import com.example.mycomposeapp.core.data.BuildConfig
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
object IgdbApiModule {

    @Provides
    @Singleton
    @IgdbOkHttpClient
    fun provideIgdbOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Client-ID", BuildConfig.IGDB_CLIENT_ID)
                .addHeader("Authorization", "Bearer ${BuildConfig.IGDB_TOKEN}")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "text/plain")
                .build()
            chain.proceed(newRequest)
        }
        .addInterceptor(loggingInterceptor)
        .build()

    @Provides
    @Singleton
    @IgdbRetrofit
    fun provideIgdbRetrofit(
        @IgdbOkHttpClient okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://api.igdb.com/v4/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }
}
