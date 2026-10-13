package com.example.apodpet.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.example.apodpet.screens.by_dates_main.ByDatesViewModel
import com.example.apodpet.screens.by_dates_main.integration.ByDatesStoreFactory
import com.example.apodpet.screens.by_dates_main.integration.data.ByDatesNasaRepository
import com.example.apodpet.screens.by_dates_main.integration.data.ByDatesNasaRepositoryImpl
import com.example.apodpet.screens.random_photo_main.RandomPhotoViewModel
import com.example.apodpet.screens.random_photo_main.integration.RandomPhotoStoreFactory
import com.example.apodpet.screens.random_photo_main.integration.data.RandomPhotosRepository
import com.example.apodpet.screens.random_photo_main.integration.data.RandomPhotosRepositoryImpl
import com.example.apodpet.screens.saved_main.SavedViewModel
import com.example.apodpet.screens.saved_main.integration.SavedStoreFactory
import com.example.apodpet.screens.saved_main.integration.data.SavedPhotosRepository
import com.example.apodpet.screens.saved_main.integration.data.SavedPhotosRepositoryImpl
import di.coreModule
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(mainModule, coreModule)
    }
}

val mainModule = module {
    viewModel { SavedViewModel(storeFactory = get(), savedState = get()) }
    viewModel { RandomPhotoViewModel(storeFactory = get()) }
    viewModel { ByDatesViewModel(storeFactory = get()) }
    factory<StoreFactory> { DefaultStoreFactory() }
    single<RandomPhotosRepository> { RandomPhotosRepositoryImpl(appClient = get(), photoDao = get()) }
    single<ByDatesNasaRepository> { ByDatesNasaRepositoryImpl(appClient = get(), photoDao = get()) }
    single<SavedPhotosRepository> { SavedPhotosRepositoryImpl(photoDao = get()) }
    factoryOf(::RandomPhotoStoreFactory)
    factoryOf(::ByDatesStoreFactory)
    factoryOf(::SavedStoreFactory)
}
