package com.example.apodpet

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.apodpet.app_routes.RandomPhoto
import com.example.apodpet.di.initKoin

fun main() = application {
    initKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "ApodPet",
    ) {

        App(
            topLevelRoute = RandomPhoto
        )
    }
}
