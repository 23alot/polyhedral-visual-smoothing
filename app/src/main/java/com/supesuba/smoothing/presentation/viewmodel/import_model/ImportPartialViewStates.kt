package com.supesuba.smoothing.presentation.viewmodel.import_model

import com.supesuba.smoothing.model.repository.ModelInfo
import com.supesuba.smoothing.presentation.viewmodel.app.AppViewState

typealias ImportPartialViewState = (ImportViewState) -> ImportViewState

object ImportPartialViewStates {

    fun loaded(models: List<ModelInfo>): ImportPartialViewState = { previousViewState ->
        previousViewState.copy(
            models = models
        )
    }

    fun init(): ImportPartialViewState = { previousViewState ->
        previousViewState
    }
}