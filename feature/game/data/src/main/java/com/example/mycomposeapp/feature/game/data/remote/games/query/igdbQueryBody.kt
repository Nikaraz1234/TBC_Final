package com.example.mycomposeapp.feature.game.data.remote.games.query

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

fun igdbQueryBody(text: String): RequestBody {
    return text.trimIndent()
        .toRequestBody("text/plain".toMediaType())
}
