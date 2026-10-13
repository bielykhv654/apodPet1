package com.example.apodpet.app_routes

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import com.example.apodpet.screens.by_dates_main.ByDatesRoute
import com.example.apodpet.screens.photo_details.PhotoDetailsRoute
import com.example.apodpet.screens.random_photo_main.RandomPhotoRoute
import com.example.apodpet.screens.saved_main.SavedRoute
import com.example.apodpet.ui.theme.AppTheme

fun EntryProviderScope<AppRoute>.screens(
    navigator: Navigator,
    onBack: () -> Unit,
) {
    entry<RandomPhoto> {
        RandomPhotoRoute(
            openDetails = { },
            openPhotoDetails = { details -> navigator.add(details) }
        )
    }
    entry<Saved> {
        SavedRoute(
            openDetails = { },
            openPhotoDetails = { details -> navigator.add(details) }
        )
    }
    entry<ByDates> {
        ByDatesRoute(
            openDetails = { },
            openPhotoDetails = { details -> navigator.add(details) }
        )
    }
    entry<PhotoDetails> { details ->
        PhotoDetailsRoute(
            details = details,
            onBack = onBack
        )
    }

}


@Composable
fun SingleScreenApp(
    route: AppRoute,
    onNavigate: (AppRoute) -> Unit,
    onGoBack: () -> Unit,
    crossTab: (AppRoute) -> Unit,
) {
    AppTheme {
        ScreenContent(
            route = route,
            onNavigate = onNavigate,
            onBack = onGoBack,
            crossTab = crossTab
        )
    }
}

/* Screen switching for iOS */
@Composable
internal fun ScreenContent(
    route: AppRoute,
    onNavigate: (AppRoute) -> Unit,
    onBack: () -> Unit,
    crossTab: (AppRoute) -> Unit
) {
    when (route) {
        RandomPhoto -> {
            RandomPhotoRoute(
                openDetails = { },
                openPhotoDetails = { details -> onNavigate(details) }
            )
        }

        is PhotoDetails -> {
            PhotoDetailsRoute(
                details = route,
                onBack = onBack
            )
        }


        Saved -> {
            SavedRoute(
                openDetails = { },
                openPhotoDetails = { details -> onNavigate(details) }
            )
        }

        ByDates -> {
            ByDatesRoute(
                openDetails = { },
                openPhotoDetails = { details -> onNavigate(details) }
            )
        }

    }
}
