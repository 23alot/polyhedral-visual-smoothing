package com.supesuba.smoothing.presentation.view.fragment

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
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
        smoothingLevelSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                Log.d("SeekBar", "change: ${p0?.progress} $p1 $p2")
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                Log.d("SeekBar", "start: ${p0?.progress}")
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                renderView.onSmoothingLevelChanged(p0?.progress ?: 1)
            }
        })
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