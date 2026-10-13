package com.example.apodpet.screens.saved_main.integration

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.apodpet.screens.saved_main.integration.SavedStore.SavedIntent
import com.example.apodpet.screens.saved_main.integration.SavedStore.SavedLabel
import com.example.apodpet.screens.saved_main.integration.SavedStore.SavedState
import com.example.apodpet.screens.saved_main.integration.data.SavedPhotosRepository
import database.NasaPhotoEntity
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

interface SavedStore : Store<SavedIntent, SavedState, SavedLabel> {

    sealed interface SavedIntent {
        data class DeletePhoto(val id: String) : SavedIntent
        data object OpenProfileDetails : SavedIntent
    }

    @Serializable
    data class SavedState(
        val savedPhotos: List<NasaPhotoEntity> = emptyList(),
        val text: String = "Saved photos"
    )

    sealed interface SavedLabel {
        data object OpenProfileDetails : SavedLabel
    }
}

class SavedStoreFactory(
    private val storeFactory: StoreFactory,
    private val savedPhotosRepository: SavedPhotosRepository
) {

    fun create(initialState: SavedState = SavedState()): SavedStore =
        object : SavedStore,
            Store<SavedIntent, SavedState, SavedLabel> by storeFactory.create(
                name = "SavedStore",
                initialState = initialState,
                bootstrapper = BootstrapperImpl(),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl
            ) {}

    private sealed interface Action {
        data object ObserveSaved : Action
    }

    private sealed interface SavedMsg {
        data class SavedPhotosUpdated(val photos: List<NasaPhotoEntity>) : SavedMsg
    }

    private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            dispatch(Action.ObserveSaved)
        }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<SavedIntent, Action, SavedState, SavedMsg, SavedLabel>() {

        override fun executeAction(action: Action) {
            when (action) {
                Action.ObserveSaved -> {
                    scope.launch {
                        savedPhotosRepository.observeSavedPhotos().collect { photos ->
                            dispatch(SavedMsg.SavedPhotosUpdated(photos))
                        }
                    }
                }
            }
        }

        override fun executeIntent(intent: SavedIntent) {
            when (intent) {
                is SavedIntent.DeletePhoto -> {
                    scope.launch {
                        savedPhotosRepository.deletePhoto(intent.id)
                    }
                }
                SavedIntent.OpenProfileDetails -> publish(SavedLabel.OpenProfileDetails)
            }
        }
    }

    private object ReducerImpl : Reducer<SavedState, SavedMsg> {
        override fun SavedState.reduce(msg: SavedMsg): SavedState =
            when (msg) {
                is SavedMsg.SavedPhotosUpdated -> copy(savedPhotos = msg.photos)
            }
    }
}
