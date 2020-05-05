package com.supesuba.smoothing.model.repository


import de.javagl.obj.Obj

interface ShaderRepository {

    suspend fun loadShader(type: Int, shaderResId: Int): Int

    suspend fun test2(): Obj

}