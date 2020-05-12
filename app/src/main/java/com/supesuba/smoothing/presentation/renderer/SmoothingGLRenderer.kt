package com.supesuba.smoothing.presentation.renderer

import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.supesuba.smoothing.*
import com.supesuba.smoothing.model.repository.ModelInfo
import com.supesuba.smoothing.model.repository.ShaderRepository
import de.javagl.obj.ObjData
import de.javagl.obj.ObjSplitting
import de.javagl.obj.ObjUtils
import kotlinx.coroutines.runBlocking
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by 23alot on 09.03.2020.
 */
class SmoothingGLRenderer(private val shaderRepository: ShaderRepository) : GLSurfaceView.Renderer {
    private var vertexLocation = 0
    private var vertexColourLocation = 0
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)

    data class EyePosition(
        val eyeX: Float = 0f, val eyeY: Float = 3f, val eyeZ: Float = 7f
    )

    private var eyePosition = EyePosition()
    private var scaleFactor: Float = 1f

    private var mProgram: Int = 0

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        val vertexShader: Int = runBlocking { shaderRepository.loadShader(GLES32.GL_VERTEX_SHADER, R.raw.vertex_shader) }
        val fragmentShader: Int = runBlocking { shaderRepository.loadShader(GLES32.GL_FRAGMENT_SHADER, R.raw.fragment_shader) }

        // create empty OpenGL ES Program
        mProgram = GLES32.glCreateProgram().also {

            // add the vertex shader to program
            GLES32.glAttachShader(it, vertexShader)

            // add the fragment shader to program
            GLES32.glAttachShader(it, fragmentShader)

            // creates OpenGL ES program executables
            GLES32.glLinkProgram(it)
        }
    }

    override fun onDrawFrame(unused: GL10) {
        // Do nothing
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        vertexLocation = GLES32.glGetAttribLocation(mProgram, "vertexPosition")
        vertexColourLocation = GLES32.glGetAttribLocation(mProgram, "vertexColour")
        GLES32.glViewport(0, 0, width, height)
        GLES32.glUseProgram(mProgram)
        val p = GLES32.glGetError()
        GLES32.glDisable(GLES32.GL_DITHER)
        val p2 = GLES32.glGetError()
        GLES32.glEnable(GLES32.GL_CULL_FACE)
        val p3 = GLES32.glGetError()
        GLES32.glCullFace(GLES32.GL_FRONT)
        val p4 = GLES32.glGetError()



        createProjectionMatrix(width, height)
        createViewMatrix()
    }

    private fun createProjectionMatrix(width: Int, height: Int) {
        var ratio = 1f
        var left = -1f
        var right = 1f
        var bottom = -1f
        var top = 1f
        val near = 2f
        val far = 10f
        if (width > height) {
            ratio = width.toFloat() / height
            left *= ratio
            right *= ratio
        } else {
            ratio = height.toFloat() / width
            bottom *= ratio
            top *= ratio
        }
        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far)
    }

    fun onScrollEvent(event: ScrollEvent) {
        eyePosition = EyePosition(
            eyeX = eyePosition.eyeX + event.dx / 10,
            eyeY = eyePosition.eyeY + event.dy / 10,
            eyeZ = 0f
        )

        setModelMatrix()
    }

    fun onScaleEvent(scaleFactor: Float) {
        this.scaleFactor = scaleFactor
        setModelMatrix()
    }

    private fun setModelMatrix() {
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.rotateM(modelMatrix, 0, eyePosition.eyeY, 1f, 0f, 0f)
        Matrix.rotateM(modelMatrix, 0, eyePosition.eyeX, 0f, 1f, 0f)
        Matrix.scaleM(modelMatrix, 0, scaleFactor, scaleFactor, scaleFactor)
    }

    private fun createViewMatrix() {
        // точка положения камеры
        val eyeX = 0f
        val eyeY = 3f
        val eyeZ = 7f

        // точка направления камеры
        val centerX = 0f
        val centerY = 0f
        val centerZ = 0f

        // up-вектор
        val upX = 0f
        val upY = 1f
        val upZ = 0f
        Matrix.setLookAtM(
            viewMatrix,
            0,
            eyeX,
            eyeY,
            eyeZ,
            centerX,
            centerY,
            centerZ,
            upX,
            upY,
            upZ
        )
    }

    fun renderFrame(renderObject: RenderObject) {
        GLES32.glClearColor(0.5f, 0.5f, 0.5f, 1.0f)
        GLES32.glClear(GLES32.GL_DEPTH_BUFFER_BIT or GLES32.GL_COLOR_BUFFER_BIT)
        setModelMatrix()
        val modelView = FloatArray(16)
        val vPMatrix = FloatArray(16)
        Matrix.multiplyMM(modelView, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, modelView, 0)

        GLES32.glGetUniformLocation(mProgram, "uMVPMatrix").also { matrix ->
            GLES32.glUniformMatrix4fv(matrix, 1, false, vPMatrix, 0)
        }
        GLES32.glGetUniformLocation(mProgram, "modelView").also { matrix ->
            GLES32.glUniformMatrix4fv(matrix, 1, false, modelView, 0)
        }

        val vBuffer = renderObject.verticesArray.toFloatBuffer()

        val nBuffer = renderObject.normalsArray.toFloatBuffer()

        val cBuffer = renderObject.colorsArray.toFloatBuffer()

        GLES32.glGetAttribLocation(mProgram, "vertexNormal").also { normalPosition ->
            GLES32.glVertexAttribPointer(normalPosition, 3, GLES32.GL_FLOAT, false, 0, nBuffer.position(0))
            GLES32.glEnableVertexAttribArray(normalPosition)
        }

        GLES32.glEnableVertexAttribArray(vertexLocation)
        GLES32.glVertexAttribPointer(vertexLocation, 3, GLES32.GL_FLOAT, false, 0, vBuffer.position(0))

        GLES32.glEnableVertexAttribArray(vertexColourLocation)
        GLES32.glVertexAttribPointer(vertexColourLocation, 3, GLES32.GL_FLOAT, false, 0, cBuffer.position(0))


        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, renderObject.verticesArray.count() / 3)
        GLES32.glFlush()
    }
}

fun FloatArray.toFloatBuffer(): FloatBuffer {
    val buffer = ByteBuffer.allocateDirect(this.size * 4)
        .order(ByteOrder.nativeOrder()).asFloatBuffer()
    buffer.put(this)
    return buffer
}

data class ScrollEvent(
    val dx: Float,
    val dy: Float
)