package com.supesuba.smoothing.model.repository

import android.content.Context
import android.content.readTextFromRaw
import com.supesuba.utils.shader.ShaderUtils

class AndroidShaderRepository(private val context: Context): ShaderRepository {

    override suspend fun loadShader(type: Int, shaderResId: Int): Int = ShaderUtils.createShader(context, type, shaderResId)

}