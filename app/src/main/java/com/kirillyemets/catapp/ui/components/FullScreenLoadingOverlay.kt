package com.kirillyemets.catapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun FullScreenLoadingOverlay(backgroundColor: Color = Color.DarkGray.copy(0.05f)) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
            .clickable(enabled = false) {  },
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}