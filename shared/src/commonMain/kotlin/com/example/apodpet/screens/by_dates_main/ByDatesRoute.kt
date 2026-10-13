package com.example.apodpet.screens.by_dates_main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import apodpet.shared.generated.resources.Res
import apodpet.shared.generated.resources.btn_retry
import com.example.apodpet.app_routes.PhotoDetails
import com.example.apodpet.screens.by_dates_main.integration.ByDatesStore
import com.example.apodpet.ui.common.LoadingAnimation
import com.example.apodpet.ui.common.NasaPhotoCard
import com.example.apodpet.ui.common.ShowNetworkResponseNotification
import com.example.apodpet.ui.common.SpaceBackground
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ByDatesRoute(
    openDetails: () -> Unit,
    openPhotoDetails: (PhotoDetails) -> Unit = {}
) {
    val viewModel = koinViewModel<ByDatesViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.labels.collect { label ->
            when (label) {
                ByDatesStore.ByDatesLabel.OpenDetails -> openDetails()
            }
        }
    }

    ByDatesScreen(
        state = state,
        onIntent = viewModel::onIntent,
        openPhotoDetails = openPhotoDetails
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ByDatesScreen(
    state: ByDatesStore.ByDatesState,
    onIntent: (ByDatesStore.ByDatesIntent) -> Unit,
    openPhotoDetails: (PhotoDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    val refreshState = lazyPagingItems.loadState.refresh

    SpaceBackground(modifier = modifier) {
        ShowNetworkResponseNotification(
            state = state,
            isLoading = {
                it.loadingState is ByDatesStore.LoadingState.Loading ||
                        refreshState is LoadState.Loading
            },
            isError = { it.loadingState is ByDatesStore.LoadingState.Error },
            errorMessage = { (it.loadingState as ByDatesStore.LoadingState.Error).error.message },
            onClose = { onIntent(ByDatesStore.ByDatesIntent.ResetLoadingState) }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical).asPaddingValues()

        ) {
            stickyHeader {
                ByDatesHeader(
                    itemCount = lazyPagingItems.itemCount,
                    isRefreshing = state.loadingState == ByDatesStore.LoadingState.Loading ||
                            refreshState is LoadState.Loading,
                    onRefresh = {
                        onIntent(ByDatesStore.ByDatesIntent.Refresh)
                    }
                )
            }

            if (lazyPagingItems.itemCount == 0 && refreshState !is LoadState.Loading) {
                item {
                    ByDatesEmptyState()
                }
            } else {
                items(
                    count = lazyPagingItems.itemCount,
                    key = { index ->
                        val photo = lazyPagingItems.peek(index)
                        photo?.date ?: photo?.url ?: index
                    }
                ) { index ->
                    val photo = lazyPagingItems[index]
                    if (photo != null) {
                        val id = photo.date ?: photo.url ?: ""
                        val isSaved = state.savedIds.contains(id)

                        NasaPhotoCard(
                            title = photo.title ?: "NASA APOD",
                            date = photo.date ?: "",
                            explanation = photo.explanation ?: "",
                            imageUrl = if (photo.mediaType == "video") (photo.url ?: "") else (photo.hdurl ?: photo.url ?: ""),
                            copyright = photo.copyright,
                            mediaType = photo.mediaType,
                            isSaved = isSaved,
                            onToggleSave = { onIntent(ByDatesStore.ByDatesIntent.ToggleSave(photo)) },
                            onPhotoClick = {
                                openPhotoDetails(
                                    PhotoDetails(
                                        title = photo.title ?: "NASA APOD",
                                        explanation = photo.explanation ?: "",
                                        imageUrl = photo.url ?: "",
                                        date = photo.date ?: "",
                                        copyright = photo.copyright,
                                        mediaType = photo.mediaType
                                    )
                                )
                            }
                        )
                    }
                }

                byDatesPagingAppendContent(
                    appendState = lazyPagingItems.loadState.append,
                    onRetry = { lazyPagingItems.retry() }
                )
            }
        }
    }
}

@Composable
private fun ByDatesHeader(
    itemCount: Int,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {
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
            Column {
                Text(
                    text = "📅 Feed  (1995 — Today)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Loaded items: $itemCount",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            OutlinedButton(
                onClick = onRefresh,
                enabled = !isRefreshing
            ) {
                Text("Refresh 🔄")
            }
        }
    }
}

@Composable
private fun ByDatesEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("No available photos", color = MaterialTheme.colorScheme.onSurface)
    }
}

private fun LazyListScope.byDatesPagingAppendContent(
    appendState: LoadState,
    onRetry: () -> Unit
) {
    when (appendState) {
        is LoadState.Loading -> {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingAnimation()
                }
            }
        }

        is LoadState.Error -> {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(onClick = onRetry) {
                        Text(stringResource(Res.string.btn_retry))
                    }
                }
            }
        }

        else -> Unit
    }
}
