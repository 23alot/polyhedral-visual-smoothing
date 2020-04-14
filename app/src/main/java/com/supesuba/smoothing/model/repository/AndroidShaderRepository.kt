package com.supesuba.smoothing.model.repository

import android.content.Context
import android.content.readTextFromRaw
import com.supesuba.smoothing.R
import com.supesuba.smoothing.trash.ObjLoader
import com.supesuba.utils.shader.ShaderUtils
import de.javagl.obj.Obj
import de.javagl.obj.ObjReader

class AndroidShaderRepository(private val context: Context): ShaderRepository {

    override suspend fun loadShader(type: Int, shaderResId: Int): Int = ShaderUtils.createShader(context, type, shaderResId)

    override suspend fun test(): ObjLoader {
        val a = context.resources.openRawResource(R.raw.aliens_apc_obj)
        return ObjLoader(context, a)
    }

    override suspend fun test2(): Obj {
        return ObjReader.read(context.resources.openRawResource(R.raw.aliens_apc_obj2))
    }

}