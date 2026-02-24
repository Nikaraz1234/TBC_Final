package com.example.mycomposeapp.core.ui.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

sealed class UiText {
    data class StringResource(
        @StringRes val resId: Int,
        val args: List<Any> = emptyList()
    ) : UiText()

    data class DynamicString(val value: String) : UiText()

    @Composable
    fun asString(): String {
        val context = LocalContext.current
        return asString(context)
    }

    fun asString(context: Context): String = when (this) {
        is StringResource -> if (args.isEmpty()) {
            context.getString(resId)
        } else {
            context.getString(resId, *args.toTypedArray())
        }
        is DynamicString -> value
    }
}
