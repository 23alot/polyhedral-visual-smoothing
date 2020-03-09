package com.supesuba.smoothing.presentation.renderer

import android.opengl.GLES32
import android.opengl.GLSurfaceView
import com.supesuba.smoothing.presentation.view.Triangle
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by 23alot on 09.03.2020.
 */
class SmoothingGLRenderer : GLSurfaceView.Renderer {
    private lateinit var triangle: Triangle

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        triangle= Triangle()
        // Set the background frame color
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    }

    override fun onDrawFrame(unused: GL10) {
        // Redraw background color
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT)
        triangle.draw()
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)
    }
}