package com.supesuba.smoothing.domain

import com.supesuba.smoothing.model.repository.ModelInfo
import com.supesuba.smoothing.model.repository.ModelRepository
import com.supesuba.smoothing.model.repository.ShaderRepository
import de.javagl.obj.Obj

class ModelInteractor(
    private val modelRepository: ModelRepository,
    private val shaderRepository: ShaderRepository
) {
    sealed class ModelEvent {
        data class Models(
            val models: List<ModelInfo>
        ): ModelEvent()

        data class ObjLoaded(
            val obj: Obj
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

    suspend fun loadModel(modelInfo: ModelInfo): ModelEvent = kotlin.runCatching {
            return@runCatching ModelEvent.ObjLoaded(
                obj = shaderRepository.getModelObj(modelInfo = modelInfo)
            )
        }.getOrElse(ModelEvent::Error)
}