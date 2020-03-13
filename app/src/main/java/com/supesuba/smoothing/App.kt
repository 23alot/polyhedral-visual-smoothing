package com.supesuba.smoothing

import android.app.Application
import com.supesuba.smoothing.di.KoinInitializer
import org.koin.core.KoinApplication

/**
 * Created by 23alot on 08.03.2020.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        KoinInitializer.init(this@App)

//        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
    }
}