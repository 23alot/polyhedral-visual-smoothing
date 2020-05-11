package com.supesuba.smoothing.router

import ru.terrakok.cicerone.Router

/**
 * Created by 23alot on 08.03.2020.
 */
class AppRouter(
    private val router: Router
) {

    fun openMainScreen() {
        router.newRootScreen(Screens.Import)
    }

    fun openPNSmoothing() {
        router.navigateTo(Screens.SmoothingPN)
    }

    fun onBack() {
        router.exit()
    }
}