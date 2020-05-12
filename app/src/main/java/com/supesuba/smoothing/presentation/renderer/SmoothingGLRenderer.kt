package com.supesuba.smoothing.presentation.renderer

import android.opengl.GLSurfaceView
import android.util.Log
import com.supesuba.smoothing.model.repository.ModelInfo
import com.supesuba.smoothing.model.repository.ShaderRepository
import com.supesuba.smoothing.presentation.view.Triangle123
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by 23alot on 09.03.2020.
 */
class SmoothingGLRenderer(private val shaderRepository: ShaderRepository) : GLSurfaceView.Renderer {
    private var triangle123: Triangle123 = Triangle123(shaderRepository)

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        triangle123.init2()
    }

    override fun onDrawFrame(unused: GL10) {
        triangle123.renderFrame()
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        triangle123.setupGraphics(width, height)
    }

    fun onScrollEvent(event: ScrollEvent) {
        triangle123.onScrollEvent(event.dx, event.dy)
    }

    fun onSmoothingLevelChanged(smoothingLevel: Int) {
        triangle123.onSmoothingLevelChanged(smoothingLevel)
    }

    fun onScaleEvent(scaleFactor: Float) {
        triangle123.onScaleEvent(scaleFactor)
    }

    fun onModelLoad(model: ModelInfo) {
        triangle123.onLoadModel(model)
    }
}

data class ScrollEvent(
    val dx: Float,
    val dy: Float
)