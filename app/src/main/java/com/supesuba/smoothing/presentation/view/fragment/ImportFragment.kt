package com.supesuba.smoothing.presentation.view.fragment

import android.content.Context
import android.content.Intent
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.supesuba.navigation.ui.BaseFragment
import com.supesuba.smoothing.R
import com.supesuba.smoothing.presentation.view.adapter.ModelAdapter
import com.supesuba.smoothing.presentation.viewmodel.import_model.ImportViewModel
import com.supesuba.smoothing.presentation.viewmodel.import_model.ImportViewState
import com.supesuba.utils.common.fileName
import kotlinx.android.synthetic.main.fragment_import.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


/**
 * Created by 23alot on 09.03.2020.
 */
class ImportFragment : BaseFragment() {
    override val layoutRes: Int = 0

    private val model: ImportViewModel by inject()

    private lateinit var modelAdapter: ModelAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_import, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fileChooser.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "text/obj" //Obj file only

            intent.addCategory(Intent.CATEGORY_OPENABLE)

            startActivityForResult(intent, 26)
        }

        modelAdapter = ModelAdapter()
        modelsRV.layoutManager = LinearLayoutManager(requireContext())
        modelsRV.adapter = modelAdapter
        GlobalScope.launch(Dispatchers.Main) {
            model.state
                .collect { updateState(it) }
        }

        GlobalScope.launch(Dispatchers.Main) {
            model.onCreate()
        }

    }

    private fun updateState(state: ImportViewState) {
        modelAdapter.setItems(state.models)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val a = File(data?.data?.encodedPath)

        val dir = requireContext().getDir("models", Context.MODE_PRIVATE)

        requireContext().contentResolver.openInputStream(data?.data!!).use { inputStream ->
            val file = File(dir, data.data?.path!!.fileName())
            FileOutputStream(file).use { outputStream ->
                val buf = ByteArray(1024)
                var len = inputStream?.read(buf) ?: 0
                while (len > 0) {
                    outputStream.write(buf, 0, len)
                    len = inputStream?.read(buf) ?: 0
                }
            }
        }
    }
}