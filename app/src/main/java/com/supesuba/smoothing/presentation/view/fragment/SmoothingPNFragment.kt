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
import com.supesuba.navigation.ui.BaseFragment
import com.supesuba.smoothing.R
import com.supesuba.utils.common.fileName
import kotlinx.android.synthetic.main.fragment_smoothing_pn.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


/**
 * Created by 23alot on 09.03.2020.
 */
class SmoothingPNFragment : BaseFragment() {
    override val layoutRes: Int = 0

    private lateinit var gLView: GLSurfaceView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_smoothing_pn, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        smoothingLevelSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                Log.d("SeekBar", "change: ${p0?.progress} $p1 $p2")
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                Log.d("SeekBar", "start: ${p0?.progress}")
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                renderView.onSmoothingLevelChanged(p0?.progress ?: 1)
            }
        })

        fileChooser.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            //intent.setType("*/*");      //all files
            //intent.setType("*/*");      //all files
            intent.type = "text/obj" //XML file only

            intent.addCategory(Intent.CATEGORY_OPENABLE)

            startActivityForResult(intent, 26)
        }
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

    override fun onResume() {
        super.onResume()
        renderView.onResume()
    }

    override fun onPause() {
        renderView.onPause()
        super.onPause()
    }
}