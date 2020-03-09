package com.supesuba.navigation.di

import org.koin.dsl.module
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router

/**
 * Created by 23alot on 09.03.2020.
 */
val navigationModule = module {
    val cicerone = Cicerone.create()
    single<Router> { cicerone.router }
    single<NavigatorHolder> { cicerone.navigatorHolder }
}