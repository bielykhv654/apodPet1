package com.example.apodpet.ui.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.apodpet.app_routes.MainNavDestination
import com.example.apodpet.app_routes.TopLevelRoute
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun SideNavigation(
    currentRoute: TopLevelRoute?,
    destinations: List<MainNavDestination<TopLevelRoute>>,
    onSelectRoute: (TopLevelRoute) -> Unit,
    expanded: Boolean,
) {
    val currentDestination = destinations.find { it.route == currentRoute }

    Row( modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)) {
        MainNavigationRail(
            currentDestination = currentDestination,
            destinations = destinations,
            onSelect = { selectedDestination ->
                onSelectRoute(selectedDestination.route)
            },
            expanded = expanded,
//            modifier = Modifier.padding(topInsetPadding()),
        )
    }
}
@Composable
fun <T : Any> MainNavigationRail(
    currentDestination: MainNavDestination<T>?,
    destinations: List<MainNavDestination<T>>,
    onSelect: (MainNavDestination<T>) -> Unit,
    expanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val width by animateDpAsState(
        if (expanded) 220.dp else 140.dp,
        animationSpec = spring(
            visibilityThreshold = Dp.VisibilityThreshold,
            stiffness = Spring.StiffnessLow
        ),
    )
    Column(
        modifier = modifier.fillMaxHeight()
            .width(width)
            .padding(12.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        destinations.forEach { destination ->
            NavRailMenuItem(
                iconResource = destination.icon,
                iconFilledResource = destination.iconSelected,
                label = destination.label,
                selected = destination == currentDestination,
                expanded = expanded,
                onClick = { onSelect(destination) },
            )
        }
    }
}
@Composable
private fun NavRailMenuItem(
    iconResource: DrawableResource,
    iconFilledResource: DrawableResource,
    label: String,
    selected: Boolean,
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val currentIcon = if (selected) iconFilledResource else iconResource

    val itemModifier = modifier
        .fillMaxWidth()
        .selectable(
            selected = selected,
            enabled = true,
            role = Role.Tab,
            onClick = onClick,
        )
        .background(
            color = if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
            shape = RoundedCornerShape(12.dp)
        )
        .padding(12.dp)

    val arrangement = Arrangement.spacedBy(8.dp)

    SharedTransitionLayout(
        modifier = itemModifier,
    ) {
        AnimatedContent(
            targetState = expanded,
            label = "NavRailItemExpanded_$label",
            modifier = Modifier.fillMaxWidth()
        ) { isExpanded ->
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                if (isExpanded) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = arrangement,
                    ) {
                        ItemElements(
                            icon = currentIcon,
                            label = label,
                            animatedVisibilityScope = this@AnimatedContent
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = arrangement,
                    ) {
                        ItemElements(
                            icon = currentIcon,
                            label = label,
                            animatedVisibilityScope = this@AnimatedContent
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SharedTransitionScope.ItemElements(
    icon: DrawableResource,
    label: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(icon),
        contentDescription = null,
        modifier = modifier
            .size(28.dp)
            .sharedBounds(
                sharedContentState = rememberSharedContentState(key = "icon_$label"),
                animatedVisibilityScope = animatedVisibilityScope,
            )
    )
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.sharedBounds(
            sharedContentState = rememberSharedContentState(key = "text_$label"),
            animatedVisibilityScope = animatedVisibilityScope,
        )
    )
}

