package com.supesuba.smoothing.presentation.view.surface

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import com.supesuba.smoothing.model.repository.ShaderRepository
import com.supesuba.smoothing.presentation.renderer.ScrollEvent
import com.supesuba.smoothing.presentation.renderer.SmoothingGLRenderer
import org.koin.core.context.GlobalContext.get

/**
 * Created by 23alot on 09.03.2020.
 */
class SmoothingGLSurfaceView(
    context: Context,
    attributeSet: AttributeSet? = null
) : GLSurfaceView(context, attributeSet) {

    private val renderer: SmoothingGLRenderer
    private val shaderRepository: ShaderRepository by get().koin.inject()
    private val gestureDetector = GestureDetector(context, GestureListener())

    init {

        setEGLContextFactory(ContextFactory())
        setEGLConfigChooser(ConfigChooser(8, 8, 8, 8, 0, 0))
        // Create an OpenGL ES 3.2 context
        renderer = SmoothingGLRenderer(shaderRepository)

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)
//        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (gestureDetector.onTouchEvent(event)) return true

        return true
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            renderer.onScrollEvent(ScrollEvent(distanceX, distanceY))
            return super.onScroll(e1, e2, distanceX, distanceY)
        }
    }
}