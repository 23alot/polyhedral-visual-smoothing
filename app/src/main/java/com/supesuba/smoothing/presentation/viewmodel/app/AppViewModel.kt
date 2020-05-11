package com.supesuba.smoothing.presentation.viewmodel.app

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.supesuba.smoothing.router.AppRouter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.scan

/**
 * Created by 23alot on 08.03.2020.
 */
class AppViewModel constructor(
    private val router: AppRouter
) : ViewModel() {
    private val stateRelay = MutableLiveData<AppPartialViewState>()

    @ExperimentalCoroutinesApi
    val state = stateRelay.asFlow()
        .scan(AppViewState()) { state, partial -> partial(state) }
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)

    fun onCreate() {
        router.openMainScreen()
    }

    fun test() {
        stateRelay.postValue(AppPartialViewStates.test())
    }

    fun onBack() {
        router.onBack()
    }
}