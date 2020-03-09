package com.supesuba.smoothing.di

import com.supesuba.navigation.di.navigationModule
import org.koin.core.context.startKoin

/**
 * Created by 23alot on 08.03.2020.
 */
object KoinInitializer {

    fun init() {
        startKoin {
            modules(
                listOf(
                    mainModule,
                    navigationModule
                )
            )
        }
    }

}