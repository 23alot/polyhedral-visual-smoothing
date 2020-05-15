package com.supesuba.smoothing.router

import android.net.Uri
import com.supesuba.smoothing.model.repository.ModelInfo
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

    fun openPNSmoothing(modelInfo: ModelInfo) {
        router.navigateTo(Screens.SmoothingPN(model = modelInfo))
    }

    fun openPhong(modelInfo: ModelInfo) {
        router.navigateTo(Screens.PhongTessellation(model = modelInfo))
    }

    fun onBack() {
        router.exit()
    }
}