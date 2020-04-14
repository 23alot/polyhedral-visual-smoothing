package com.supesuba.smoothing.model.repository

import com.supesuba.smoothing.trash.ObjLoader
import de.javagl.obj.Obj

interface ShaderRepository {

    suspend fun loadShader(type: Int, shaderResId: Int): Int

    suspend fun test(): ObjLoader

    suspend fun test2(): Obj

}