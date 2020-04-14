package com.supesuba.smoothing.presentation.renderer

import android.opengl.GLES20
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import com.supesuba.smoothing.model.repository.ShaderRepository
import com.supesuba.smoothing.presentation.view.Triangle
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by 23alot on 09.03.2020.
 */
class SmoothingGLRenderer(private val shaderRepository: ShaderRepository) : GLSurfaceView.Renderer {
    private var triangle: Triangle = Triangle(shaderRepository)

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        triangle.init2()
    }

    override fun onDrawFrame(unused: GL10) {
        triangle.renderFrame()
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        triangle.setupGraphics(width, height)
    }
}