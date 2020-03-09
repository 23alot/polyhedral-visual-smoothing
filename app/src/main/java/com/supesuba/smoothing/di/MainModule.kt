package com.supesuba.smoothing.di

import com.supesuba.smoothing.presentation.viewmodel.AppViewModel
import com.supesuba.smoothing.router.AppRouter
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by 23alot on 09.03.2020.
 */
val mainModule = module {
    single { AppRouter(get()) }
    viewModel { AppViewModel(get()) }
}