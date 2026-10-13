package com.example.apodpet.ui.theme

import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ThemeToggleButton(
    modifier: Modifier = Modifier
) {
    val themeController = LocalThemeController.current
    IconButton(
        onClick = { themeController.toggleTheme() },
        modifier = modifier
    ) {
        Text(
            text = if (themeController.isDark) "☀️" else "🌙",
            style = MaterialTheme.typography.titleMedium
        )
    }
}
