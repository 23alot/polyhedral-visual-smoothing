package com.supesuba.smoothing.router

import androidx.fragment.app.Fragment
import com.supesuba.smoothing.presentation.view.fragment.MainFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

/**
 * Created by 23alot on 09.03.2020.
 */
object Screens {
    object Main: SupportAppScreen() {
        override fun getFragment(): Fragment = MainFragment()
    }
}