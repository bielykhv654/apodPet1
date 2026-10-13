package com.example.apodpet.screens.random_photo_main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.apodpet.screens.random_photo_main.integration.RandomPhotoStore
import com.example.apodpet.ui.common.NasaPhotoCard
import com.example.apodpet.ui.common.ShowNetworkResponseNotification
import com.example.apodpet.ui.common.SpaceBackground
import com.example.apodpet.ui.theme.ThemeToggleButton
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RandomPhotoRoute(
    openDetails: () -> Unit,
    openPhotoDetails: (PhotoDetails) -> Unit = {}
) {
    val viewModel = koinViewModel<RandomPhotoViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.labels.collect { label ->
            when (label) {
                RandomPhotoStore.RandomPhotoLabel.OpenDetails -> openDetails()
            }
        }
    }

    RandomPhotoScreen(
        state = state,
        onIntent = viewModel::onIntent,
        openPhotoDetails = openPhotoDetails
    )
}

@Composable
private fun RandomPhotoScreen(
    state: RandomPhotoStore.RandomPhotoState,
    onIntent: (RandomPhotoStore.RandomPhotoIntent) -> Unit,
    openPhotoDetails: (PhotoDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    SpaceBackground(modifier = modifier) {
        ShowNetworkResponseNotification(
            state = state,
            isError = { it.loadingState is RandomPhotoStore.LoadingState.Error },
            errorMessage = { (it.loadingState as RandomPhotoStore.LoadingState.Error).error.message },
            isLoading = { it.loadingState is RandomPhotoStore.LoadingState.Loading },
            onClose = { onIntent(RandomPhotoStore.RandomPhotoIntent.ResetLoadingState) }
        )
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
                            text = "🎲 Random photos",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ThemeToggleButton()
                            OutlinedButton(
                                onClick = { onIntent(RandomPhotoStore.RandomPhotoIntent.LoadRandom) },
                                enabled = state.loadingState != RandomPhotoStore.LoadingState.Loading
                            ) {
                                Text("Get another🔄")
                            }
                        }
                    }
                }
            }

            items(
                items = state.photos,
                key = { it.date ?: it.url ?: it.hashCode().toString() }
            ) { photo ->
                val id = photo.date ?: photo.url ?: ""
                val isSaved = state.savedIds.contains(id)

                NasaPhotoCard(
                    photo = photo.toApodPhoto(),
                    isSaved = isSaved,
                    onToggleSave = { onIntent(RandomPhotoStore.RandomPhotoIntent.ToggleSave(photo)) },
                    onPhotoClick = { openPhotoDetails(PhotoDetails(photo)) }
                )
            }
        }
    }
}
