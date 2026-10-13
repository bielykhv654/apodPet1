package com.example.apodpet.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp

enum class WindowSize {
    COMPACT, // Phones
    MEDIUM,  // Tablets / Small Desktop
    EXPANDED // Large Desktop
}

val LocalWindowSize = compositionLocalOf { WindowSize.COMPACT }

@Composable
fun ProvideWindowSize(content: @Composable () -> Unit) {
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    val widthDp = with(density) { windowInfo.containerSize.width.toDp() }

    val size = when {
        widthDp < 600.dp -> WindowSize.COMPACT
        widthDp < 900.dp -> WindowSize.MEDIUM
        else -> WindowSize.EXPANDED
    }
    CompositionLocalProvider(LocalWindowSize provides size) {
        content()
    }
}
