package com.example.apodpet.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import apodpet.shared.generated.resources.Res
import apodpet.shared.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource


@Composable
fun <T> ShowNetworkResponseNotification(
    state: T,
    isError: (T) -> Boolean,
    isLoading: (T) -> Boolean,
    errorMessage: (T) -> StringResource,
    onClose: () -> Unit
) {
    when {
        isError(state) -> {
            val visibleState =
                remember { MutableTransitionState(false).apply { targetState = true } }
            Popup(
                alignment = Alignment.TopCenter,
                onDismissRequest = {
                    onClose()
                    visibleState.targetState = false
                }
            ) {
                AnimatedVisibility(
                    visibleState = visibleState,
                    enter = fadeIn(animationSpec = tween(500)) + slideIn(
                        animationSpec = tween(500),
                        initialOffset = { IntOffset(0, it.height) }
                    ),
                    exit = fadeOut(animationSpec = tween(500)) + slideOut(
                        animationSpec = tween(500),
                        targetOffset = { IntOffset(0, it.height) }
                    )
                ) {
                    ErrorDialog(
                        message = stringResource(errorMessage(state)),
                        onDismiss = {
                            onClose()
                            visibleState.targetState = false
                        }
                    )
                }
            }
        }

        isLoading(state) ->  LoadingAnimation()
    }
}

@Composable
private fun ErrorDialog(message: String, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                imageVector = vectorResource(Res.drawable.compose_multiplatform),
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterVertically).size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.weight(1f)) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface

                )
            }
            Text(
                text = "Ок",
                modifier = Modifier.padding(horizontal = 16.dp).clickable(onClick = onDismiss),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
