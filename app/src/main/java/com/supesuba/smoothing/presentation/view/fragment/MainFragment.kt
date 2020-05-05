package com.supesuba.smoothing.presentation.view.fragment

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.supesuba.navigation.ui.BaseFragment
import com.supesuba.smoothing.R
import com.supesuba.smoothing.model.repository.ShaderRepository
import com.supesuba.smoothing.presentation.view.surface.SmoothingGLSurfaceView
import kotlinx.android.synthetic.main.fragment_smoothing_pn.*
import org.koin.android.ext.android.get

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
        return inflater.inflate(R.layout.fragment_smoothing_pn, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        renderView.onResume()
    }

    override fun onPause() {
        renderView.onPause()
        super.onPause()
    }
}