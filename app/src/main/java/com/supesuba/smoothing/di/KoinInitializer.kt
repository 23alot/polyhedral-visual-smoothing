package com.supesuba.smoothing.di

import android.content.Context
import com.supesuba.navigation.di.navigationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Created by 23alot on 08.03.2020.
 */
object KoinInitializer {

    fun init(context: Context) {
        startKoin {
            androidContext(context)
            modules(
                listOf(
                    mainModule,
                    navigationModule
                )
            )
        }
    }

}