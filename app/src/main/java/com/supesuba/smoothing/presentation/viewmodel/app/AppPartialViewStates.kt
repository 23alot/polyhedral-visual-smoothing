package com.supesuba.smoothing.presentation.viewmodel.app

/**
 * Created by 23alot on 08.03.2020.
 */
typealias AppPartialViewState = (AppViewState) -> AppViewState

object AppPartialViewStates {

    fun test(): AppPartialViewState = { previousViewState ->
        previousViewState.copy(
            test = false
        )
    }
}