package com.example.apodpet.ui.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.apodpet.ui.theme.LocalThemeController

@Composable
fun SpaceBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = LocalThemeController.current.isDark
    val animSpec = tween<Color>(durationMillis = 500)

    val topColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFF090A12) else Color(0xFFF5F6FC),
        animationSpec = animSpec,
        label = "bgTop"
    )
    val midColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFF13172E) else Color(0xFFE8EBF8),
        animationSpec = animSpec,
        label = "bgMid"
    )
    val botColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFF0D0F1B) else Color(0xFFF0F2FA),
        animationSpec = animSpec,
        label = "bgBot"
    )

    val spaceBrush = Brush.verticalGradient(
        colors = listOf(topColor, midColor, botColor)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(spaceBrush),
        content = content
    )
}
