package com.example.apodpet.app_routes

import apodpet.shared.generated.resources.Res
import apodpet.shared.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.DrawableResource

val bottomNavDestinations: List<MainNavDestination<TopLevelRoute>> = listOf(
    MainNavDestination(
        label = "Random",
        route = RandomPhoto,
        icon = Res.drawable.compose_multiplatform
    ),
    MainNavDestination(
        label = "Feed",
        route = ByDates,
        icon = Res.drawable.compose_multiplatform
    ),
    MainNavDestination(
        label = "Saved",
        route = Saved,
        icon = Res.drawable.compose_multiplatform
    )
)

data class MainNavDestination<T : Any>(
    val label: String,
    val icon: DrawableResource,
    val route: T,
    val iconSelected: DrawableResource = icon,
)
