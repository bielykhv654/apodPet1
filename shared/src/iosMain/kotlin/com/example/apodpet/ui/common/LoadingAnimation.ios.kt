package com.example.apodpet.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import platform.UIKit.UIActivityIndicatorView
import platform.UIKit.UIActivityIndicatorViewStyleMedium
import platform.UIKit.UIColor

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun LoadingAnimation() {
    val color = MaterialTheme.colorScheme.onSurface
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        UIKitView(
            properties = UIKitInteropProperties(placedAsOverlay = true),
            factory = {
                val indicator = UIActivityIndicatorView()
                indicator.activityIndicatorViewStyle = UIActivityIndicatorViewStyleMedium
                indicator.color = color.toUIColor()
                indicator.startAnimating()
                indicator
            }
        )
    }


}

fun Color.toUIColor(): UIColor =
    UIColor(
        red = red.toDouble(),
        green = green.toDouble(),
        blue = blue.toDouble(),
        alpha = alpha.toDouble(),
    )
