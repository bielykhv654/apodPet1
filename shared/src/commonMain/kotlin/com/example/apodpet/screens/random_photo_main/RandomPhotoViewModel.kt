package com.example.apodpet.screens.random_photo_main

import androidx.lifecycle.ViewModel
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.apodpet.screens.random_photo_main.integration.RandomPhotoStore
import com.example.apodpet.screens.random_photo_main.integration.RandomPhotoStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class RandomPhotoViewModel(storeFactory: RandomPhotoStoreFactory) : ViewModel() {

    private val store = storeFactory.create()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<RandomPhotoStore.RandomPhotoState> = store.stateFlow

    val labels = store.labels

    fun onIntent(intent: RandomPhotoStore.RandomPhotoIntent) {
        store.accept(intent)
    }

    override fun onCleared() {
        store.dispose()
        super.onCleared()
    }
}
