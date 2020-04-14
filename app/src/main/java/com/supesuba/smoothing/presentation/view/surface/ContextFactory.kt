package com.supesuba.smoothing.presentation.view.surface

import android.opengl.GLSurfaceView.EGLContextFactory
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay


/**
 * Created by 23alot on 16.03.2020.
 */

class ContextFactory : EGLContextFactory {
    override fun createContext(
        egl: EGL10,
        display: EGLDisplay?,
        eglConfig: EGLConfig?
    ): EGLContext {
        val EGL_CONTEXT_CLIENT_VERSION = 0x3098
        val attrib_list = intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE)
        return egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list)
    }

    override fun destroyContext(egl: EGL10, display: EGLDisplay?, context: EGLContext?) {
        egl.eglDestroyContext(display, context)
    }
}