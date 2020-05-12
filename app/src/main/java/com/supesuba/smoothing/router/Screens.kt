package com.supesuba.smoothing.router

import androidx.fragment.app.Fragment
import com.supesuba.smoothing.di.PNTriangle
import com.supesuba.smoothing.model.repository.ModelInfo
import com.supesuba.smoothing.presentation.view.fragment.ImportFragment
import com.supesuba.smoothing.presentation.view.fragment.SmoothingPNFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

/**
 * Created by 23alot on 09.03.2020.
 */
object Screens {
    data class SmoothingPN(
        private val model: ModelInfo
    ): SupportAppScreen() {
        override fun getFragment(): Fragment = SmoothingPNFragment.newInstance(model, PNTriangle)
    }

    object Import: SupportAppScreen() {
        override fun getFragment(): Fragment = ImportFragment.newInstance()
    }
}