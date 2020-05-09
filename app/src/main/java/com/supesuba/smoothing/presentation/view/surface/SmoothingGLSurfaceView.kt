package com.supesuba.smoothing.presentation.view.surface

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.supesuba.smoothing.model.repository.ShaderRepository
import com.supesuba.smoothing.presentation.renderer.ScrollEvent
import com.supesuba.smoothing.presentation.renderer.SmoothingGLRenderer
import org.koin.core.context.GlobalContext.get
import kotlin.math.max
import kotlin.math.min

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
    private val scaleGestureDetector = ScaleGestureDetector(context, GestureListener())

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

        var result = scaleGestureDetector.onTouchEvent(event)
        result = gestureDetector.onTouchEvent(event) || result
        return result || super.onTouchEvent(event)
    }

    fun onSmoothingLevelChanged(smoothingLevel: Int) {
        renderer.onSmoothingLevelChanged(smoothingLevel)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener(), ScaleGestureDetector.OnScaleGestureListener {

        private var scaleFactor: Float = 1f

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            if (scaleGestureDetector.isInProgress) return false
            renderer.onScrollEvent(ScrollEvent(distanceX, distanceY))
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        override fun onScaleBegin(p0: ScaleGestureDetector?): Boolean {
            return true
        }

        override fun onScaleEnd(p0: ScaleGestureDetector?) {

        }

        override fun onScale(p0: ScaleGestureDetector?): Boolean {
            scaleFactor *= p0?.scaleFactor ?: 1f
            scaleFactor = max(0.1f,
                min(scaleFactor, 10.0f)
            )
            renderer.onScaleEvent(scaleFactor)
            return true
        }
    }
}