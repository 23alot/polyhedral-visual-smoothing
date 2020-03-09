package com.supesuba.smoothing.presentation.view.activity

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.doOnApplyWindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.supesuba.navigation.ui.BaseFragment
import com.supesuba.smoothing.R
import com.supesuba.smoothing.presentation.viewmodel.AppViewModel
import com.supesuba.smoothing.presentation.viewmodel.AppViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command

/**
 * Created by 23alot on 08.03.2020.
 */
class AppActivity : AppCompatActivity() {
    private val navigatorHolder: NavigatorHolder by inject()
    private val model: AppViewModel by inject()

    private val navigator: Navigator =
        object : SupportAppNavigator(this, supportFragmentManager, R.id.container) {
            override fun setupFragmentTransaction(
                command: Command?,
                currentFragment: Fragment?,
                nextFragment: Fragment?,
                fragmentTransaction: FragmentTransaction
            ) {
                // Fix incorrect order lifecycle callback of MainFragment
                fragmentTransaction.setReorderingAllowed(true)
            }
        }

    private val currentFragment: BaseFragment?
    get() = supportFragmentManager.findFragmentById(R.id.container) as? BaseFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.container).doOnApplyWindowInsets { view, insets, initialPadding ->
            view.updatePadding(
                left = initialPadding.left + insets.systemWindowInsetLeft,
                right = initialPadding.right + insets.systemWindowInsetRight
            )
            insets.replaceSystemWindowInsets(
                Rect(
                    0,
                    insets.systemWindowInsetTop,
                    0,
                    insets.systemWindowInsetBottom
                )
            )
        }

        GlobalScope.launch(Dispatchers.Main) {
            model.state
                .collect { updateState(it) }
        }

        model.onCreate()

    }

    private fun updateState(state: AppViewState) {

    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onBackPressed() {
        model.onBack()
    }
}
