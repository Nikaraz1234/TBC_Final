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
object MalApiModule {

    @Provides
    @Singleton
    @MalOkHttpClient
    fun provideMalOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .addHeader("X-MAL-CLIENT-ID", BuildConfig.MAL_CLIENT_ID)
                .build()
            chain.proceed(request)
        }
        .addInterceptor(loggingInterceptor)
        .applyDefaultTimeouts()
        .build()

    @Provides
    @Singleton
    @MalRetrofit
    fun provideMalRetrofit(
        @MalOkHttpClient okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://api.myanimelist.net/v2/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }
}