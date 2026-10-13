package com.example.apodpet.screens.by_dates_main.integration

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.apodpet.StringMessage
import com.example.apodpet.screens.by_dates_main.integration.ByDatesStore.ByDatesIntent
import com.example.apodpet.screens.by_dates_main.integration.ByDatesStore.ByDatesLabel
import com.example.apodpet.screens.by_dates_main.integration.ByDatesStore.ByDatesState
import com.example.apodpet.screens.by_dates_main.integration.data.ByDatesNasaRepository
import database.NasaPhotoDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

interface ByDatesStore : Store<ByDatesIntent, ByDatesState, ByDatesLabel> {

    sealed interface ByDatesIntent {
        data class ToggleSave(val photo: NasaPhotoDto) : ByDatesIntent
        data object OpenDetails : ByDatesIntent
        data object Refresh : ByDatesIntent
        data object ResetLoadingState : ByDatesIntent
    }

    data class ByDatesState(
        val pagingDataFlow: Flow<PagingData<NasaPhotoDto>> = emptyFlow(),
        val savedIds: Set<String> = emptySet(),
        val loadingState: LoadingState = LoadingState.Initial
    )

    sealed interface LoadingState {
        data object Initial : LoadingState
        data object Loading : LoadingState
        data class Error(val error: StringMessage) : LoadingState
    }

    sealed interface ByDatesLabel {
        data object OpenDetails : ByDatesLabel
    }
}

class ByDatesStoreFactory(
    private val storeFactory: StoreFactory,
    private val byDatesNasaRepository: ByDatesNasaRepository
) {

    fun create(): ByDatesStore =
        object : ByDatesStore, Store<ByDatesIntent, ByDatesState, ByDatesLabel> by storeFactory.create(
            name = "ByDatesStore",
            initialState = ByDatesState(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data object Init : Action
    }

    private sealed interface Msg {
        data class PagingFlowCreated(val pagingDataFlow: Flow<PagingData<NasaPhotoDto>>) : Msg
        data class SavedIdsUpdated(val savedIds: Set<String>) : Msg
        data class Error(val error: StringMessage) : Msg
        data object Loading : Msg
        data object ResetLoadingState : Msg
    }

    private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            dispatch(Action.Init)
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<ByDatesIntent, Action, ByDatesState, Msg, ByDatesLabel>() {

        private var currentPagingSource: PagingSource<String, NasaPhotoDto>? = null

        private val pagerFlow: Flow<PagingData<NasaPhotoDto>> by lazy {
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    initialLoadSize = 10,
                    prefetchDistance = 1,
                    maxSize = 100,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    byDatesNasaRepository.getPhotosPagingSource(
                        onError = { error ->
                            dispatch(Msg.Error(error))
                        }
                    ).also { currentPagingSource = it }
                }
            ).flow.cachedIn(scope)
        }

        override fun executeAction(action: Action) {
            when (action) {
                Action.Init -> {
                    dispatch(Msg.PagingFlowCreated(pagerFlow))
                    observeSaved()
                }
            }
        }

        override fun executeIntent(intent: ByDatesIntent) {
            when (intent) {
                is ByDatesIntent.ToggleSave -> toggleSave(intent.photo)
                ByDatesIntent.OpenDetails -> publish(ByDatesLabel.OpenDetails)
                ByDatesIntent.Refresh -> currentPagingSource?.invalidate()
                ByDatesIntent.ResetLoadingState -> dispatch(Msg.ResetLoadingState)
            }
        }

        private fun observeSaved() {
            scope.launch {
                byDatesNasaRepository.observeSavedPhotos().collect { savedEntities ->
                    val ids = savedEntities.map { it.id }.toSet()
                    dispatch(Msg.SavedIdsUpdated(ids))
                }
            }
        }

        private fun toggleSave(photo: NasaPhotoDto) {
            val id = photo.date ?: photo.url ?: return
            scope.launch {
                if (state().savedIds.contains(id)) {
                    byDatesNasaRepository.deletePhoto(id)
                } else {
                    byDatesNasaRepository.savePhoto(photo)
                }
            }
        }
    }

    private object ReducerImpl : Reducer<ByDatesState, Msg> {
        override fun ByDatesState.reduce(msg: Msg): ByDatesState {
            return when (msg) {
                is Msg.PagingFlowCreated -> copy(pagingDataFlow = msg.pagingDataFlow)
                is Msg.SavedIdsUpdated -> copy(savedIds = msg.savedIds)
                is Msg.Error -> copy(loadingState = ByDatesStore.LoadingState.Error(msg.error))
                Msg.Loading -> copy(loadingState = ByDatesStore.LoadingState.Loading)
                Msg.ResetLoadingState -> copy(loadingState = ByDatesStore.LoadingState.Initial)
            }
        }
    }
}
