package com.supesuba.smoothing.di

import com.supesuba.smoothing.domain.ModelInteractor
import com.supesuba.smoothing.model.repository.AndroidModelRepository
import com.supesuba.smoothing.model.repository.AndroidShaderRepository
import com.supesuba.smoothing.model.repository.ModelRepository
import com.supesuba.smoothing.model.repository.ShaderRepository
import com.supesuba.smoothing.presentation.viewmodel.app.AppViewModel
import com.supesuba.smoothing.presentation.viewmodel.import_model.ImportViewModel
import com.supesuba.smoothing.router.AppRouter
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.koin.experimental.builder.singleBy

/**
 * Created by 23alot on 09.03.2020.
 */
val mainModule = module {
    single { AppRouter(get()) }
    single { ModelInteractor(get()) }
    viewModel { AppViewModel(get()) }
    viewModel { ImportViewModel(get(), get()) }
    singleBy<ShaderRepository, AndroidShaderRepository>()
    singleBy<ModelRepository, AndroidModelRepository>()
}