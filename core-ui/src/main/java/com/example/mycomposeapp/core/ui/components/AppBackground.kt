package com.example.mycomposeapp.core.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.mycomposeapp.core.ui.R

@Composable
fun AppBackground(
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = R.drawable.app_background,
        contentDescription = null,
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}
