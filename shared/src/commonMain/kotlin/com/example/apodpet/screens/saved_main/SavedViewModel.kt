package com.example.apodpet.screens.saved_main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.apodpet.screens.saved_main.integration.SavedStore
import com.example.apodpet.screens.saved_main.integration.SavedStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.Json

class SavedViewModel(storeFactory: SavedStoreFactory, savedState: SavedStateHandle) : ViewModel() {

    private val store = storeFactory.create(
        initialState = savedState.get<String>(KEY_STATE)
            ?.let { Json.decodeFromString<SavedStore.SavedState>(it) }
            ?: SavedStore.SavedState()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<SavedStore.SavedState> = store.stateFlow
    val labels = store.labels

    init {
        state
            .onEach { savedState[KEY_STATE] = Json.encodeToString(it) }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: SavedStore.SavedIntent) { store.accept(intent) }

    override fun onCleared() {
        store.dispose()
        super.onCleared()
    }

    private companion object {
        const val KEY_STATE = "SavedStoreState"
    }
}
