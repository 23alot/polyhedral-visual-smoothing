package com.supesuba.smoothing.presentation.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.supesuba.navigation.ui.BaseFragment
import com.supesuba.smoothing.R
import com.supesuba.smoothing.model.repository.ModelInfo
import com.supesuba.smoothing.presentation.viewmodel.import_model.ImportViewModel
import com.supesuba.smoothing.presentation.viewmodel.import_model.ImportViewState
import com.supesuba.smoothing.presentation.viewmodel.smoothing_pn.SmoothingViewModel
import kotlinx.android.synthetic.main.fragment_smoothing_pn.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.StringQualifier


/**
 * Created by 23alot on 09.03.2020.
 */
class SmoothingPNFragment : BaseFragment() {
    override val layoutRes: Int = 0

    private val model: SmoothingViewModel by viewModel { parametersOf(arguments?.getString(QUALIFIER) ?: throw IllegalArgumentException("No smoothing algorithm provided")) }

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


        val modelInfo = arguments?.getParcelable<ModelInfo>(MODEL)
        loadModel(modelInfo!!)
        model.subscribe { state -> showState(state) }
    }

    private fun showState(state: ImportViewState) {

    }

    fun loadModel(model: ModelInfo) {
        renderView.onLoadModel(model)
    }

    override fun onResume() {
        super.onResume()
        renderView.onResume()
    }

    override fun onPause() {
        renderView.onPause()
        super.onPause()
    }

    companion object {

        private const val MODEL = "MODEL"
        private const val QUALIFIER = "QUALIFIER"

        fun newInstance(model: ModelInfo, qualifier: StringQualifier): SmoothingPNFragment {
            val fragment = SmoothingPNFragment()
            val bundle = Bundle()
            bundle.putParcelable(MODEL, model)
            bundle.putString(QUALIFIER, qualifier.value)
            fragment.arguments = bundle

            return fragment
        }
    }
}