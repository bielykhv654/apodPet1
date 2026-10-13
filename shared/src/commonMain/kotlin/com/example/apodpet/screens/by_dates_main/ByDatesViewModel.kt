package com.example.apodpet.screens.by_dates_main

import androidx.lifecycle.ViewModel
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.apodpet.screens.by_dates_main.integration.ByDatesStore
import com.example.apodpet.screens.by_dates_main.integration.ByDatesStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

class ByDatesViewModel(storeFactory: ByDatesStoreFactory) : ViewModel() {

    private val store = storeFactory.create()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<ByDatesStore.ByDatesState> = store.stateFlow

    val labels = store.labels

    fun onIntent(intent: ByDatesStore.ByDatesIntent) {
        store.accept(intent)
    }

    override fun onCleared() {
        store.dispose()
        super.onCleared()
    }
}
