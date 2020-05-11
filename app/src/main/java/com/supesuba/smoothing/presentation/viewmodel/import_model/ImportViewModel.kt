package com.supesuba.smoothing.presentation.viewmodel.import_model

import androidx.lifecycle.ViewModel
import com.supesuba.smoothing.domain.ModelInteractor
import com.supesuba.smoothing.router.AppRouter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.scan

/**
 * Created by 23alot on 11.05.2020.
 */
class ImportViewModel constructor(
    private val router: AppRouter,
    private val modelInteractor: ModelInteractor
) : ViewModel() {
    @ExperimentalCoroutinesApi
    private val stateFlow = MutableStateFlow<ImportPartialViewState>(ImportPartialViewStates.init())


    @ExperimentalCoroutinesApi
    val state = stateFlow
        .scan(ImportViewState()) { state, partial -> partial(state) }
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)

    suspend fun onCreate() {
        val event = modelInteractor.getModels()
        when (event) {
            is ModelInteractor.ModelEvent.Models -> stateFlow.value = ImportPartialViewStates.loaded(event.models)
        }
    }

    fun onBack() {
        router.onBack()
    }
}