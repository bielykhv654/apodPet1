package com.example.apodpet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.example.apodpet.app_routes.AppRoute
import com.example.apodpet.app_routes.SingleScreenApp
import com.example.apodpet.app_routes.TopLevelRoute
import platform.UIKit.UIViewController

@Suppress("unused") // Called from Swift
fun MainViewController(topLevelRoute: TopLevelRoute): UIViewController = ComposeUIViewController(
    configure = { onFocusBehavior = OnFocusBehavior.DoNothing },
) {
    App(topLevelRoute = topLevelRoute)
}

@Suppress("unused") // Called from Swift
fun MainViewController(
    topLevelRoute: TopLevelRoute,
    onNavigate: (AppRoute) -> Unit,
    onActivate: (AppRoute) -> Unit,
): UIViewController = ComposeUIViewController(
    configure = { onFocusBehavior = OnFocusBehavior.DoNothing },
) {
    ScreenLifecycle {
        SingleScreenApp(
            route = topLevelRoute,
            onNavigate = onNavigate,
            onGoBack = { },
            crossTab = onActivate
        )
    }
}

@Suppress("unused") // Called from Swift
fun ScreenViewController(
    route: AppRoute,
    onNavigate: (AppRoute) -> Unit,
    onGoBack: () -> Unit,
    crossTab: (AppRoute) -> Unit,
): UIViewController = ComposeUIViewController(
    configure = { onFocusBehavior = OnFocusBehavior.DoNothing },
) {
    ScreenLifecycle {
        SingleScreenApp(
            route = route,
            onNavigate = onNavigate,
            onGoBack = onGoBack,
            crossTab = crossTab
        )
    }
}

/* Clears the ViewModelStore when leaving the screen so ViewModels are destroyed */
@Composable
fun ScreenLifecycle(
    content: @Composable () -> Unit
) {
    val owner = checkNotNull(LocalViewModelStoreOwner.current)

    DisposableEffect(owner) {
        onDispose {
            owner.viewModelStore.clear()
        }
    }
    content()
}
