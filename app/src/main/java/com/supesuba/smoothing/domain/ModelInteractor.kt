package com.supesuba.smoothing.domain

import com.supesuba.smoothing.model.repository.ModelInfo
import com.supesuba.smoothing.model.repository.ModelRepository

class ModelInteractor(
    private val modelRepository: ModelRepository
) {
    sealed class ModelEvent {
        data class Models(
            val models: List<ModelInfo>
        ): ModelEvent()

        data class Error(val throwable: Throwable): ModelEvent()
    }

    suspend fun getModels(): ModelEvent {
        return kotlin.runCatching {
            val models = modelRepository.getModels()
            ModelEvent.Models(
                models = models
            )
        }.getOrElse(ModelEvent::Error)
    }
}