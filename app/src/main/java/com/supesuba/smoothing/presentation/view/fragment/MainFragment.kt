package com.supesuba.smoothing.presentation.view.fragment

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.supesuba.navigation.ui.BaseFragment
import com.supesuba.smoothing.presentation.view.surface.SmoothingGLSurfaceView

/**
 * Created by 23alot on 09.03.2020.
 */
class MainFragment : BaseFragment() {
    override val layoutRes: Int = 0

    private lateinit var gLView: GLSurfaceView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        gLView = SmoothingGLSurfaceView(requireContext())
        return gLView
    }
}