package com.supesuba.smoothing.model.repository

interface ShaderRepository {

    suspend fun loadShader(type: Int, shaderResId: Int): Int

}