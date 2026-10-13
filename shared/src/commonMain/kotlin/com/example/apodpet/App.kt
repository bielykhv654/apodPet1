package com.example.apodpet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.apodpet.app_routes.AppRoute
import com.example.apodpet.app_routes.ByDates
import com.example.apodpet.app_routes.MainNavDestination
import com.example.apodpet.app_routes.NavState
import com.example.apodpet.app_routes.Navigator
import com.example.apodpet.app_routes.RandomPhoto
import com.example.apodpet.app_routes.Saved
import com.example.apodpet.app_routes.TopLevelRoute
import com.example.apodpet.app_routes.bottomNavDestinations
import com.example.apodpet.app_routes.rememberNavState
import com.example.apodpet.app_routes.screens
import com.example.apodpet.ui.common.SideNavigation
import com.example.apodpet.ui.theme.AppTheme
import com.example.apodpet.ui.theme.LocalWindowSize
import com.example.apodpet.ui.theme.ProvideWindowSize
import com.example.apodpet.ui.theme.WindowSize
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource


@Composable
fun App(
    topLevelRoute: TopLevelRoute,
    onNavigate: ((AppRoute) -> Unit)? = null,
    onActivate: ((TopLevelRoute) -> Unit)? = null,
) {
    AppTheme {
        ProvideWindowSize {
            MainContent(
                startRoute = topLevelRoute,
                onActivate = onActivate,
                onNavigate = onNavigate
            )
        }
    }
}

@Composable
private fun MainContent(
    startRoute: AppRoute,
    onNavigate: ((AppRoute) -> Unit)? = null,
    onActivate: ((TopLevelRoute) -> Unit)? = null,
) {

    val navState = rememberNavState(
        startRoute = startRoute,
        primaryTopLevelRoute = RandomPhoto,
        topLevelRoutes = setOf(
            RandomPhoto,
            ByDates,
            Saved
        )
    )
    val topLevelBackEnabled = remember { true }
    val navigator = remember(navState, topLevelBackEnabled) {
        Navigator(navState, topLevelBackEnabled)
    }

    if (onNavigate != null) {
        LaunchedEffect(navState) {
            snapshotFlow { navState.currentBackstack.toList() }.collect { backstack: List<AppRoute> ->
                val detailRoutes = backstack.drop(1)
                if (detailRoutes.isNotEmpty()) {
                    detailRoutes.forEach { onNavigate(it) }
                    navState.currentBackstack.removeRange(1, navState.currentBackstack.size)
                }
            }
        }
    }

    if (onActivate != null) {
        LaunchedEffect(navState) {
            snapshotFlow { navState.topLevelRoute }.collect { route: TopLevelRoute? ->
                if (route != null) onActivate(route)
            }
        }
    }

    val entryProvider = entryProvider {
        screens(
            navigator = navigator,
            onBack = { navigator.goBack() },
        )
    }

    val content = @Composable {
        NavDisplay(
            entries = navState.toDecoratedEntries(entryProvider),
            onBack = { navigator.goBack() }
        )
    }
    if (isNative) {
        content()
    } else {
        NavScaffold(
            navState = navState,
            navigator = navigator,
            content = content,
        )
    }
}


@Composable
private fun NavScaffold(
    navState: NavState,
    navigator: Navigator,
    content: @Composable (() -> Unit)
) {
    val currentRoute = navState.currentBackstack.lastOrNull()
    val isTopLevel = currentRoute is TopLevelRoute
    val windowSize = LocalWindowSize.current
    val useSideNavigation = isDesktop && windowSize != WindowSize.COMPACT
    val showSideNav = isDesktop && useSideNavigation
    val showBottomNav = !useSideNavigation && (isDesktop || isTopLevel)
    val onSelectRoute: (TopLevelRoute) -> Unit = { route ->
        navigator.activate(route)
    }

    val enterAnimSpec =
        remember { tween<IntSize>(delayMillis = AnimationConstants.DefaultDurationMillis) }

    Row(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = showSideNav,
        ) {
            SideNavigation(
                currentRoute = navState.topLevelRoute,
                destinations = bottomNavDestinations,
                onSelectRoute = onSelectRoute,
                expanded = windowSize == WindowSize.EXPANDED
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clipToBounds()
            ) {
                content()
            }

            AnimatedVisibility(
                visible = showBottomNav,
                enter = expandVertically(enterAnimSpec),
                exit = shrinkVertically() + fadeOut(),
            ) {
                BottomNavigation(
                    currentRoute = navState.topLevelRoute,
                    destinations = bottomNavDestinations,
                    onSelectRoute = onSelectRoute,
                )
            }
        }
    }
}

@Composable
private fun BottomNavigation(
    currentRoute: TopLevelRoute?,
    destinations: List<MainNavDestination<TopLevelRoute>>,
    onSelectRoute: (TopLevelRoute) -> Unit,
) {
    val currentDestination = destinations.find { it.route == currentRoute }
    Column {
        MainNavigationBar(
            currentDestination = currentDestination,
            destinations = destinations,
            onSelect = { selectedDestination ->
                onSelectRoute(selectedDestination.route)
            },
        )
    }
}

@Composable
fun <T : Any> MainNavigationBar(
    currentDestination: MainNavDestination<T>?,
    destinations: List<MainNavDestination<T>>,
    onSelect: (MainNavDestination<T>) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        destinations.forEach { destination ->
            MainNavigationButton(
                iconResource = destination.icon,
                iconFilledResource = destination.iconSelected,
                contentDescription = destination.label,
                selected = destination == currentDestination,
                onClick = { onSelect(destination) },
                modifier = Modifier
                    .widthIn(max = 150.dp)
                    .fillMaxWidth()
                    .weight(1f, fill = false),
            )
        }
    }
}

@Composable
private fun MainNavigationButton(
    iconResource: DrawableResource,
    iconFilledResource: DrawableResource,
    contentDescription: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .selectable(
                selected = selected,
                enabled = true,
                role = Role.Tab,
                onClick = onClick,
            )
            .background(
                color = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.padding(1.dp).size(28.dp),
            painter = painterResource(if (selected) iconFilledResource else iconResource),
            contentDescription = contentDescription,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = contentDescription,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


