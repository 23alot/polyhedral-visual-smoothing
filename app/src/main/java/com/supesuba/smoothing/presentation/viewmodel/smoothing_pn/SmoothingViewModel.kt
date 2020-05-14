package com.supesuba.smoothing.presentation.viewmodel.smoothing_pn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supesuba.smoothing.RenderObject
import com.supesuba.smoothing.Triangle
import com.supesuba.smoothing.Vertex
import com.supesuba.smoothing.di.Test
import com.supesuba.smoothing.domain.ModelInteractor
import com.supesuba.smoothing.domain.SmoothingInteractor
import com.supesuba.smoothing.model.repository.ModelInfo
import com.supesuba.smoothing.presentation.viewmodel.import_model.ImportPartialViewState
import com.supesuba.smoothing.presentation.viewmodel.import_model.ImportPartialViewStates
import com.supesuba.smoothing.presentation.viewmodel.import_model.ImportViewState
import com.supesuba.smoothing.router.AppRouter
import com.supesuba.smoothing.toVertexList
import de.javagl.obj.Obj
import de.javagl.obj.ObjData
import de.javagl.obj.ObjUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Created by 23alot on 11.05.2020.
 */
class SmoothingViewModel constructor(
    private val router: AppRouter,
    private val modelInteractor: ModelInteractor,
    private val smoothingInteractor: SmoothingInteractor
) : ViewModel() {

    fun subscribe(block: suspend CoroutineScope.(SmoothingViewState) -> Unit) {
        viewModelScope.launch {
            smoothingInteractor.observeModelChanges()
                .flowOn(Dispatchers.Main)
                .map { renderObject -> SmoothingViewState(renderObject) }
                .collect { block(this, it) }
        }
    }

    fun onCreate(modelInfo: ModelInfo) {
        viewModelScope.launch {
            val event = modelInteractor.loadModel(modelInfo)
            when (event) {
                is ModelInteractor.ModelEvent.ObjLoaded -> onObjLoaded(event.obj)
            }
        }
    }

    private suspend fun onObjLoaded(obj: Obj) {
        val a3 = ObjUtils.triangulate(obj)
        val a4 = ObjUtils.makeNormalsUnique(a3)
        val a5 = ObjUtils.convertToRenderable(a4)

        val r4 = ObjData.getVerticesArray(a5)
        val points = r4.toVertexList()
        for (i in 0 until obj.numFaces) {
            val face = obj.getFace(i)
            val l = mutableListOf<Int>()
            val tv = mutableListOf<Vertex>()
            for (z in 0 until face.numVertices) {
                val vert = face.getVertexIndex(z)
                tv += points[vert]
                l += vert
            }

            val t = Triangle(
                v1 = tv[0],
                v2 = tv[1],
                v3 = tv[2]
            )

            smoothingInteractor.addTriangle(t)
        }

        smoothingInteractor.calculateVertexNormals(vertices = points)
        smoothingInteractor.calculateSupportPoints()
    }

    fun onTessellationLevelChanged(tessellationLevel: Int) {
        viewModelScope.launch {
            smoothingInteractor.tessellate(tessellationLevel)
        }

    }

    fun onBack() {
        router.onBack()
    }
}