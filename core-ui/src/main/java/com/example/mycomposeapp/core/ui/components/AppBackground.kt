package com.example.mycomposeapp.core.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.mycomposeapp.core.ui.R as CoreUiR // adjust if your drawables are in another module

@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val dark = isSystemInDarkTheme()

    @DrawableRes val bgRes =
        if (dark) CoreUiR.drawable.app_background2
        else CoreUiR.drawable.app_bg_11

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(bgRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        content()
    }
}