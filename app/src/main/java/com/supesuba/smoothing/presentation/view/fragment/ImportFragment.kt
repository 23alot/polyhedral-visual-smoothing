package com.supesuba.smoothing.presentation.view.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.supesuba.navigation.ui.BaseFragment
import com.supesuba.smoothing.R
import com.supesuba.smoothing.model.repository.ModelInfo
import com.supesuba.smoothing.presentation.view.adapter.ModelAdapter
import com.supesuba.smoothing.presentation.viewmodel.import_model.ImportViewModel
import com.supesuba.smoothing.presentation.viewmodel.import_model.ImportViewState
import com.supesuba.utils.common.fileName
import kotlinx.android.synthetic.main.fragment_import.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream


/**
 * Created by 23alot on 09.03.2020.
 */
class ImportFragment : BaseFragment(), ModelAdapter.OnModelClickListener {
    override val layoutRes: Int = 0

    private val model: ImportViewModel by viewModel()

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
        modelAdapter.listener = this
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


    override fun onModelSelected(modelInfo: ModelInfo) {
        val adapter = ArrayAdapter<String>(requireContext(), R.layout.item_algorithm, arrayOf("PN", "Phong"))
        AlertDialog.Builder(requireContext())
            .setAdapter(adapter) { _, b ->
                when(b) {
                    0 -> model.onPNSelected(modelInfo)
                    1 -> model.onPhongSelected(modelInfo)
                }
            }
            .create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // TODO: проверка request и result code
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

        GlobalScope.launch(Dispatchers.Main) {
            model.onCreate()
        }
    }

    companion object {

        fun newInstance(): ImportFragment = ImportFragment()
    }
}