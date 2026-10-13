package com.example.apodpet.app_routes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute {
    val topLevelRoute: TopLevelRoute
    val backstackPath: List<AppRoute>
        get() = listOf(this)
}

@Serializable
sealed interface TopLevelRoute : AppRoute {
    override val topLevelRoute: TopLevelRoute
        get() = this
}

@Serializable
@SerialName("RandomPhoto")
data object RandomPhoto : TopLevelRoute

@Serializable
@SerialName("ByDates")
data object ByDates : TopLevelRoute

@Serializable
@SerialName("Saved")
data object Saved : TopLevelRoute

@Serializable
data class ApodPhoto(
    val title: String = "",
    val explanation: String = "",
    val imageUrl: String = "",
    val date: String = "",
    val copyright: String? = null,
    val mediaType: String? = null,
)

@Serializable
@SerialName("PhotoDetails")
data class PhotoDetails(
    val photo: ApodPhoto
) : AppRoute {
    override val topLevelRoute: TopLevelRoute = RandomPhoto
}
