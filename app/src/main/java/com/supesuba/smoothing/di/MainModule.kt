package com.supesuba.smoothing.di

import com.supesuba.smoothing.domain.ModelInteractor
import com.supesuba.smoothing.domain.PNTriangleSmoothingInteractor
import com.supesuba.smoothing.domain.PhongSmoothingInteractor
import com.supesuba.smoothing.domain.SmoothingInteractor
import com.supesuba.smoothing.model.repository.AndroidModelRepository
import com.supesuba.smoothing.model.repository.AndroidShaderRepository
import com.supesuba.smoothing.model.repository.ModelRepository
import com.supesuba.smoothing.model.repository.ShaderRepository
import com.supesuba.smoothing.presentation.viewmodel.app.AppViewModel
import com.supesuba.smoothing.presentation.viewmodel.import_model.ImportViewModel
import com.supesuba.smoothing.presentation.viewmodel.smoothing_pn.SmoothingViewModel
import com.supesuba.smoothing.router.AppRouter
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.StringQualifier
import org.koin.dsl.module
import org.koin.experimental.builder.singleBy

/**
 * Created by 23alot on 09.03.2020.
 */
val mainModule = module {
    single { AppRouter(get()) }
    single { ModelInteractor(get(), get()) }
    viewModel { AppViewModel(get()) }
    singleBy<SmoothingInteractor, PhongSmoothingInteractor>(PhongTessellation)
    singleBy<SmoothingInteractor, PNTriangleSmoothingInteractor>(PNTriangle)
    viewModel { ImportViewModel(get(), get()) }
    viewModel { (algo: StringQualifier) -> SmoothingViewModel(get(), get(), get(algo)) }
    singleBy<ShaderRepository, AndroidShaderRepository>()
    singleBy<ModelRepository, AndroidModelRepository>()
}
