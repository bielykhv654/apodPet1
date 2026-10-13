package com.example.apodpet.screens.random_photo_main.integration

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.apodpet.StringMessage
import com.example.apodpet.screens.random_photo_main.integration.RandomPhotoStore.RandomPhotoIntent
import com.example.apodpet.screens.random_photo_main.integration.RandomPhotoStore.RandomPhotoLabel
import com.example.apodpet.screens.random_photo_main.integration.RandomPhotoStore.RandomPhotoState
import com.example.apodpet.screens.random_photo_main.integration.data.RandomPhotosRepository
import com.example.apodpet.screens.random_photo_main.integration.data.RandomPhotosResult
import database.NasaPhotoDto
import kotlinx.coroutines.launch

interface RandomPhotoStore : Store<RandomPhotoIntent, RandomPhotoState, RandomPhotoLabel> {

    sealed interface RandomPhotoIntent {
        data object LoadRandom : RandomPhotoIntent
        data class ToggleSave(val photo: NasaPhotoDto) : RandomPhotoIntent
        data object OpenDetails : RandomPhotoIntent
        data object ResetLoadingState : RandomPhotoIntent
    }

    data class RandomPhotoState(
        val photos: List<NasaPhotoDto> = emptyList(),
        val savedIds: Set<String> = emptySet(),
        val loadingState: LoadingState = LoadingState.Initial
    )

    sealed interface LoadingState {
        data object Initial : LoadingState
        data object Loading : LoadingState
        data class Error(val error: StringMessage) : LoadingState
    }

    sealed interface RandomPhotoLabel {
        data object OpenDetails : RandomPhotoLabel
    }
}

class RandomPhotoStoreFactory(
    private val storeFactory: StoreFactory,
    private val randomPhotosRepository: RandomPhotosRepository
) {

    fun create(): RandomPhotoStore =
        object : RandomPhotoStore, Store<RandomPhotoIntent, RandomPhotoState, RandomPhotoLabel> by storeFactory.create(
            name = "RandomPhotoStore",
            initialState = RandomPhotoState(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data object Init : Action
    }

    private sealed interface Msg {
        data object Loading : Msg
        data class PhotosLoaded(val photos: List<NasaPhotoDto>) : Msg
        data class SavedIdsUpdated(val savedIds: Set<String>) : Msg
        data class Error(val message: StringMessage) : Msg
        data object ResetLoadingState : Msg
    }

    private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            dispatch(Action.Init)
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<RandomPhotoIntent, Action, RandomPhotoState, Msg, RandomPhotoLabel>() {
        override fun executeAction(action: Action) {
            when (action) {
                Action.Init -> {
                    observeSaved()
                    loadRandom()
                }
            }
        }

        override fun executeIntent(intent: RandomPhotoIntent) {
            when (intent) {
                RandomPhotoIntent.LoadRandom -> loadRandom()
                is RandomPhotoIntent.ToggleSave -> toggleSave(intent.photo)
                RandomPhotoIntent.OpenDetails -> publish(RandomPhotoLabel.OpenDetails)
                RandomPhotoIntent.ResetLoadingState -> dispatch(Msg.ResetLoadingState)
            }
        }

        private fun observeSaved() {
            scope.launch {
                randomPhotosRepository.observeSavedPhotos().collect { savedEntities ->
                    val ids = savedEntities.map { it.id }.toSet()
                    dispatch(Msg.SavedIdsUpdated(ids))
                }
            }
        }

        private fun loadRandom() {
            dispatch(Msg.Loading)
            scope.launch {
                when (val result = randomPhotosRepository.getRandomPhotos(10)) {
                    is RandomPhotosResult.Error -> dispatch(Msg.Error(result.error))
                    is RandomPhotosResult.Success -> dispatch(Msg.PhotosLoaded(result.photoList))
                }
            }
        }

        private fun toggleSave(photo: NasaPhotoDto) {
            val id = photo.date ?: photo.url ?: return
            scope.launch {
                if (state().savedIds.contains(id)) {
                    randomPhotosRepository.deletePhoto(id)
                } else {
                    randomPhotosRepository.savePhoto(photo)
                }
            }
        }
    }

    private object ReducerImpl : Reducer<RandomPhotoState, Msg> {
        override fun RandomPhotoState.reduce(msg: Msg): RandomPhotoState =
            when (msg) {
                Msg.Loading -> copy(loadingState = RandomPhotoStore.LoadingState.Loading)
                is Msg.PhotosLoaded -> copy(photos = msg.photos, loadingState = RandomPhotoStore.LoadingState.Initial)
                is Msg.SavedIdsUpdated -> copy(savedIds = msg.savedIds)
                is Msg.Error -> copy(loadingState = RandomPhotoStore.LoadingState.Error(msg.message))
                Msg.ResetLoadingState -> copy(loadingState = RandomPhotoStore.LoadingState.Initial)
            }
    }
}
