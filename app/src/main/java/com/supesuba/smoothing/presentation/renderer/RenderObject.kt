package com.supesuba.smoothing.presentation.renderer

import android.opengl.GLES32
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class RenderObject(
    val verticesArray: FloatArray,
    val normalsArray: FloatArray,
    val colorsArray: FloatArray,
    val program: Int,
    val vertexPosition: Int,
    val colorPosition: Int
) {
    fun render() {
        render(
            vArray = verticesArray,
            nArray = normalsArray,
            cArray = colorsArray
        )
    }

    private fun render(
        vArray: FloatArray,
        nArray: FloatArray,
        cArray: FloatArray
    ) {
        val vBuffer = ByteBuffer.allocateDirect(vArray.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        vBuffer.put(vArray)

        val nBuffer = ByteBuffer.allocateDirect(nArray.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        nBuffer.put(nArray)

        val cBuffer = ByteBuffer.allocateDirect(cArray.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        cBuffer.put(cArray)

        val aw9 = GLES32.glGetError()

        GLES32.glGetAttribLocation(program, "vertexNormal").also { normalPosition ->
            GLES32.glVertexAttribPointer(normalPosition, 3, GLES32.GL_FLOAT, false, 0, nBuffer.position(0))
            GLES32.glEnableVertexAttribArray(normalPosition)
        }
        val aw10 = GLES32.glGetError()

        GLES32.glEnableVertexAttribArray(vertexPosition)
        GLES32.glVertexAttribPointer(vertexPosition, 3, GLES32.GL_FLOAT, false, 0, vBuffer.position(0))
        val aw11 = GLES32.glGetError()

        GLES32.glEnableVertexAttribArray(colorPosition)
        GLES32.glVertexAttribPointer(colorPosition, 3, GLES32.GL_FLOAT, false, 0, cBuffer.position(0))

        val aw12 = GLES32.glGetError()

        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, vArray.count() / 3)
        val aw13 = GLES32.glGetError()
        val a = 0
    }
}