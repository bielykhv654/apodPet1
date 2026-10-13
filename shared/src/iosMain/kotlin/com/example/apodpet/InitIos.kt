package com.example.apodpet

import com.example.apodpet.di.mainModule
import di.coreModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

fun initialize(onKoinStart: KoinApplication.() -> Unit) {
    startKoin {
        onKoinStart()
        modules(
            mainModule, coreModule
        )
    }
}