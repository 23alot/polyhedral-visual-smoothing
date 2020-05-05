package com.supesuba.smoothing.presentation.view.surface

import android.content.Context
import android.opengl.GLSurfaceView
import com.supesuba.smoothing.model.repository.ShaderRepository
import com.supesuba.smoothing.presentation.renderer.SmoothingGLRenderer

/**
 * Created by 23alot on 09.03.2020.
 */
class SmoothingGLSurfaceView(context: Context, shaderRepository: ShaderRepository) : GLSurfaceView(context) {

    private val renderer: SmoothingGLRenderer

    init {

        setEGLContextFactory(ContextFactory())
        setEGLConfigChooser(ConfigChooser(8,8,8,8, 0 ,0))
        // Create an OpenGL ES 3.2 context

        renderer = SmoothingGLRenderer(shaderRepository)

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)
    }
}