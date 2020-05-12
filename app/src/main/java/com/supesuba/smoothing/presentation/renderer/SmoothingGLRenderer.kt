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

    private var renderObject: RenderObject? = null

    data class EyePosition(
        val eyeX: Float = 0f, val eyeY: Float = 3f, val eyeZ: Float = 7f
    )

    private var eyePosition = EyePosition()
    private var scaleFactor: Float = 1f

    private lateinit var figure: Figure

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
        renderFrame()
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        vertexLocation = GLES32.glGetAttribLocation(mProgram, "vertexPosition")
        vertexColourLocation = GLES32.glGetAttribLocation(mProgram, "vertexColour")
//        GLES32.glDepthMask(true)
        GLES32.glViewport(0, 0, width, height)
        GLES32.glUseProgram(mProgram)
        val p = GLES32.glGetError()
//        GLES32.glDisable(GLES32.GL_CULL_FACE)
        GLES32.glDisable(GLES32.GL_DITHER)
        val p2 = GLES32.glGetError()
        GLES32.glEnable(GLES32.GL_CULL_FACE)
        val p3 = GLES32.glGetError()
        GLES32.glCullFace(GLES32.GL_FRONT)
        val p4 = GLES32.glGetError()
//        GLES32.glFrontFace(GLES32.GL_CCW)



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

    fun onSmoothingLevelChanged(smoothingLevel: Int) {
        figure.tessellatePN(tessellationLevel = smoothingLevel)
        val r10 = figure.toVertexList().toFloatArray()
        val r5 = r10.count()
        val r12 = figure.toNormalList().toFloatArray()

        val colors = FloatArray(3 * r5)
        val color = floatArrayOf(0f, 0f, 0f)
        for (i in 0 until 3 * r5) {
            colors[i] = color[i % color.size]
        }

        renderObject = RenderObject(
            verticesArray = r10,
            normalsArray = r12,
            colorsArray = colors,
            vertexPosition = vertexLocation,
            colorPosition = vertexColourLocation,
            program = mProgram
        )

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

    fun onModelLoad(modelInfo: ModelInfo) {
        val obj = runBlocking { shaderRepository.getModelObj(modelInfo) }
        val objs = ObjSplitting.splitByMaxNumVertices(obj, 65000).map { ob ->
//            val a2 = ObjUtils.makeVertexIndexed(ob)
            val a3 = ObjUtils.triangulate(ob)
            val a4 = ObjUtils.makeNormalsUnique(a3)
            val a5 = ObjUtils.convertToRenderable(a4)
            return@map a5
        }


        val r4 = ObjData.getVerticesArray(objs[0])
        val points = r4.toVertexList()
        figure = Figure()

        for (i in 0 until objs[0].numFaces) {
            val face = objs[0].getFace(i)
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

            figure.addTriangle(t)
        }

        figure.calculateVertexNormals(points)
        figure.tessellatePN(tessellationLevel = 1)

        val r10 = figure.toVertexList().toFloatArray()
        val r5 = r10.count()
        val r11 = ByteBuffer.allocateDirect(r10.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        r11.put(r10)

//        val r123 = ObjData.getNormalsArray(obj)
        val r12 = figure.toNormalList().toFloatArray()
        val r13 = ByteBuffer.allocateDirect(r12.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        r13.put(r12)

        val color = floatArrayOf(0f, 0f, 0f)
        val colors = FloatArray(3 * r5)
        for (i in 0 until 3 * r5) {
            colors[i] = color[i % color.size]
        }

        renderObject = RenderObject(
            verticesArray = r10,
            normalsArray = r12,
            colorsArray = colors,
            vertexPosition = vertexLocation,
            colorPosition = vertexColourLocation,
            program = mProgram
        )
    }

    fun renderFrame() {
        GLES32.glClearColor(0.5f, 0.5f, 0.5f, 1.0f)
        GLES32.glClear(GLES32.GL_DEPTH_BUFFER_BIT or GLES32.GL_COLOR_BUFFER_BIT)
        setModelMatrix()
        renderFrame2()

        GLES32.glFlush()
    }

    fun renderFrame2() {
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

        renderObject?.render()
    }




}

data class ScrollEvent(
    val dx: Float,
    val dy: Float
)