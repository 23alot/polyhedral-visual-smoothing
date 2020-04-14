package com.supesuba.smoothing.presentation.view.surface


import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLDisplay

/**
 * Created by 23alot on 15.03.2020.
 */
class ConfigChooser: GLSurfaceView.EGLConfigChooser
{
    protected var redSize = 8
    protected var greenSize = 8
    protected var blueSize = 8
    protected var alphaSize = 8
    protected var depthSize = 16
    protected var sampleSize = 4
    protected var stencilSize = 0
    protected var value = IntArray(1)

    override fun chooseConfig(egl: EGL10, display: EGLDisplay): EGLConfig?
    {
        val EGL_OPENGL_ES2_BIT = 4;
        val configAttributes = intArrayOf(
            EGL10.EGL_RED_SIZE, redSize,
            EGL10.EGL_GREEN_SIZE, greenSize,
            EGL10.EGL_BLUE_SIZE, blueSize,
            EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
            EGL10.EGL_SAMPLES, sampleSize,
            EGL10.EGL_DEPTH_SIZE, depthSize,
            EGL10.EGL_STENCIL_SIZE, stencilSize,
            EGL10.EGL_NONE
        )
        val  num_config = IntArray(1)
        egl.eglChooseConfig(display, configAttributes, null, 0, num_config)
        val numConfigs = num_config [0]
        val configs =
            arrayOfNulls<EGLConfig>(numConfigs)
        egl.eglChooseConfig(display, configAttributes, configs, numConfigs, num_config);
        return selectConfig(egl, display, configs)
    }

    fun selectConfig(
        egl: EGL10,
        display: EGLDisplay,
        configs: Array<EGLConfig?>
    ): EGLConfig? {
        for (config in configs) {
            val d: Int = getConfigAttrib(egl, display, config, EGL10.EGL_DEPTH_SIZE, 0)
            val s: Int = getConfigAttrib(egl, display, config, EGL10.EGL_GREEN_SIZE, 0)
            val r: Int = getConfigAttrib(egl, display, config, EGL10.EGL_RED_SIZE, 0)
            val g: Int = getConfigAttrib(egl, display, config, EGL10.EGL_GREEN_SIZE, 0)
            val b: Int = getConfigAttrib(egl, display, config, EGL10.EGL_BLUE_SIZE, 0)
            val a: Int = getConfigAttrib(egl, display, config, EGL10.EGL_ALPHA_SIZE, 0)
            if (r == redSize && g == greenSize && b == blueSize && a == alphaSize && d >= depthSize && s >= stencilSize) return config
        }
        return null
    }

    private fun getConfigAttrib(
        egl: EGL10,
        display: EGLDisplay,
        config: EGLConfig?,
        attribute: Int,
        defaultValue: Int
    ): Int {
        return if (egl.eglGetConfigAttrib(
                display,
                config,
                attribute,
                value
            )
        ) value[0] else defaultValue
    }
}