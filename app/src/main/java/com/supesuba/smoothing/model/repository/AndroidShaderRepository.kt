package com.supesuba.smoothing.model.repository

import android.content.Context
import com.supesuba.utils.shader.ShaderUtils
import de.javagl.obj.Obj
import de.javagl.obj.ObjReader

class AndroidShaderRepository(private val context: Context) : ShaderRepository {

    override suspend fun loadShader(type: Int, shaderResId: Int): Int = ShaderUtils.createShader(context, type, shaderResId)

    override suspend fun getModelObj(modelInfo: ModelInfo): Obj {
        context.contentResolver.openInputStream(modelInfo.uri).use { modelInputStream ->
            return ObjReader.read(modelInputStream)
        }
    }

}