package com.supesuba.smoothing.router

import androidx.fragment.app.Fragment
import com.supesuba.smoothing.presentation.view.fragment.ImportFragment
import com.supesuba.smoothing.presentation.view.fragment.SmoothingPNFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

/**
 * Created by 23alot on 09.03.2020.
 */
object Screens {
    object SmoothingPN: SupportAppScreen() {
        override fun getFragment(): Fragment = SmoothingPNFragment()
    }

    object Import: SupportAppScreen() {
        override fun getFragment(): Fragment = ImportFragment()
    }
}