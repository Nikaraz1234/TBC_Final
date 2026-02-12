package com.example.mycomposeapp.core.data.common

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object NetworkConstants {
    const val TIMEOUT_SECONDS = 30L
}

fun OkHttpClient.Builder.applyDefaultTimeouts(): OkHttpClient.Builder = this
    .connectTimeout(NetworkConstants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
    .readTimeout(NetworkConstants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
    .writeTimeout(NetworkConstants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
