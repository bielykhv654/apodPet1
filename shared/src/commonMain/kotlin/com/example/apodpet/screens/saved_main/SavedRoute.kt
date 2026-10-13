package com.example.apodpet.screens.saved_main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.apodpet.PhotoDetails
import com.example.apodpet.app_routes.PhotoDetails
import com.example.apodpet.toApodPhoto
import com.example.apodpet.screens.saved_main.integration.SavedStore
import com.example.apodpet.ui.common.NasaPhotoCard
import com.example.apodpet.ui.common.SpaceBackground
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SavedRoute(
    openDetails: () -> Unit,
    openPhotoDetails: (PhotoDetails) -> Unit = {}
) {
    val viewModel = koinViewModel<SavedViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.labels.collect { label ->
            when (label) {
                SavedStore.SavedLabel.OpenProfileDetails -> openDetails()
            }
        }
    }

    SavedScreen(
        state = state,
        onIntent = viewModel::onIntent,
        openPhotoDetails = openPhotoDetails
    )
}

@Composable
private fun SavedScreen(
    state: SavedStore.SavedState,
    onIntent: (SavedStore.SavedIntent) -> Unit,
    openPhotoDetails: (PhotoDetails) -> Unit
) {
    SpaceBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical).asPaddingValues()
        ) {
            stickyHeader {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💾 Saved photos (${state.savedPhotos.size})",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (state.savedPhotos.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                text = "📷 No saved photos",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Press ❤️ on photos to save it here.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(
                    items = state.savedPhotos,
                    key = { it.id }
                ) { photo ->
                    NasaPhotoCard(
                        photo = photo.toApodPhoto(),
                        isSaved = true,
                        onDelete = { onIntent(SavedStore.SavedIntent.DeletePhoto(photo.id)) },
                        onPhotoClick = { openPhotoDetails(PhotoDetails(photo)) }
                    )
                }
            }
        }
    }
}
