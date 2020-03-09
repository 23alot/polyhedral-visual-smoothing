package com.supesuba.smoothing.presentation.view.surface

import android.content.Context
import android.opengl.GLSurfaceView
import com.supesuba.smoothing.presentation.renderer.SmoothingGLRenderer

/**
 * Created by 23alot on 09.03.2020.
 */
class SmoothingGLSurfaceView(context: Context) : GLSurfaceView(context) {

    private val renderer: SmoothingGLRenderer

    init {

        // Create an OpenGL ES 3.2 context
        setEGLContextClientVersion(3)

        renderer = SmoothingGLRenderer()

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)
    }
}