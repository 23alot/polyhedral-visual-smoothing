package com.supesuba.smoothing.model.repository

import android.content.Context
import androidx.core.net.toUri
import com.supesuba.utils.common.fileName

class AndroidModelRepository(
    private val context: Context
) : ModelRepository {

    override suspend fun getModels(): List<ModelInfo> {
        val dir = context.getDir(MODELS_FOLDER, Context.MODE_PRIVATE)
        return dir.listFiles()?.mapNotNull { modelFile ->
            val name = modelFile.path.fileName()
            ModelInfo(
                name = name,
                id = name.hashCode().toLong(),
                uri = modelFile.toUri()
            )
        } ?: throw NoModels
    }

    companion object {
        private const val MODELS_FOLDER = "models"
    }

    object NoModels : Throwable()
}