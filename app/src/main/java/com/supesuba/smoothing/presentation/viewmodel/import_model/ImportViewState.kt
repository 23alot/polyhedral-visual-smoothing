package com.supesuba.smoothing.presentation.viewmodel.import_model

import com.supesuba.smoothing.model.repository.ModelInfo

data class ImportViewState(
    val models: List<ModelInfo> = emptyList()
)