package com.example.mycomposeapp.core.data.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GoogleBooksRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GoogleBooksOkHttpClient
