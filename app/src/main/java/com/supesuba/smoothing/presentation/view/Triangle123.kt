package com.supesuba.smoothing.presentation.view

import android.opengl.GLES32
import android.opengl.Matrix
import com.supesuba.smoothing.Figure
import com.supesuba.smoothing.R
import com.supesuba.smoothing.Triangle
import com.supesuba.smoothing.Vertex
import com.supesuba.smoothing.model.repository.ShaderRepository
import com.supesuba.smoothing.trash.RenderObject
import com.supesuba.smoothing.trash.TriangleWithIndices
import de.javagl.obj.Obj
import de.javagl.obj.ObjData
import de.javagl.obj.ObjSplitting
import de.javagl.obj.ObjUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer


/**
 * Created by 23alot on 09.03.2020.
 */
// number of coordinates per vertex in this array
const val COORDS_PER_VERTEX = 3
var triangleCoords = floatArrayOf(     // in counterclockwise order:
    0.0f, 0.622008459f, 0.0f,      // top
    -0.5f, -0.311004243f, 0.0f,    // bottom left
    0.5f, -0.311004243f, 0.0f      // bottom right
)

val cubeVertices = floatArrayOf(
    -1.0f, 1.0f, -1.0f, /* Back. */
    1.0f, 1.0f, -1.0f,
    -1.0f, -1.0f, -1.0f,
    1.0f, -1.0f, -1.0f,
    -1.0f, 1.0f, 1.0f, /* Front. */
    1.0f, 1.0f, 1.0f,
    -1.0f, -1.0f, 1.0f,
    1.0f, -1.0f, 1.0f,
    -1.0f, 1.0f, -1.0f, /* Left. */
    -1.0f, -1.0f, -1.0f,
    -1.0f, -1.0f, 1.0f,
    -1.0f, 1.0f, 1.0f,
    1.0f, 1.0f, -1.0f, /* Right. */
    1.0f, -1.0f, -1.0f,
    1.0f, -1.0f, 1.0f,
    1.0f, 1.0f, 1.0f,
    -1.0f, -1.0f, -1.0f, /* Top. */
    -1.0f, -1.0f, 1.0f,
    1.0f, -1.0f, 1.0f,
    1.0f, -1.0f, -1.0f,
    -1.0f, 1.0f, -1.0f, /* Bottom. */
    -1.0f, 1.0f, 1.0f,
    1.0f, 1.0f, 1.0f,
    1.0f, 1.0f, -1.0f
)

val colour = floatArrayOf(
    1.0f, 0.0f, 0.0f,
    1.0f, 0.0f, 0.0f,
    1.0f, 0.0f, 0.0f,
    1.0f, 0.0f, 0.0f,
    0.0f, 1.0f, 0.0f,
    0.0f, 1.0f, 0.0f,
    0.0f, 1.0f, 0.0f,
    0.0f, 1.0f, 0.0f,
    0.0f, 0.0f, 1.0f,
    0.0f, 0.0f, 1.0f,
    0.0f, 0.0f, 1.0f,
    0.0f, 0.0f, 1.0f,
    1.0f, 1.0f, 0.0f,
    1.0f, 1.0f, 0.0f,
    1.0f, 1.0f, 0.0f,
    1.0f, 1.0f, 0.0f,
    0.0f, 1.0f, 1.0f,
    0.0f, 1.0f, 1.0f,
    0.0f, 1.0f, 1.0f,
    0.0f, 1.0f, 1.0f,
    1.0f, 0.0f, 1.0f,
    1.0f, 0.0f, 1.0f,
    1.0f, 0.0f, 1.0f,
    1.0f, 0.0f, 1.0f
)

val indices = shortArrayOf(
    0, 2, 3, 0, 1, 3, 4, 6, 7, 4, 5, 7, 8, 9,
    10, 11, 8, 10, 12, 13, 14, 15, 12, 14, 16, 17, 18, 16, 19,
    18, 20, 21, 22, 20, 23, 22
)


class Triangle123(private val shaderRepository: ShaderRepository) {

    // Set color with red, green, blue and alpha (opacity) values
    val color = floatArrayOf(0f, 0f, 0f)

    private var mProgram: Int = 0

    fun init2() {

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

    private val state: MutableStateFlow<Int> = MutableStateFlow(5)



    private var vertexLocation = 0
    private var vertexColourLocation = 0
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)

    var p3: ShortArray = shortArrayOf()

    var a: FloatBuffer? = null
    var b: FloatBuffer? = null
    var c: ShortBuffer? = null
    private lateinit var objs: List<Obj>

    var r13: FloatBuffer? = null
    var r11: FloatBuffer? = null
    var r5: Int = 0

    private lateinit var figure: Figure

    fun setupGraphics(width: Int, height: Int) {
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
//        val obj = runBlocking { shaderRepository.getModelObj() }
//        obj = ObjUtils.makeVertexIndexed(obj)
//        objs = ObjSplitting.splitByMaxNumVertices(obj, 65000)
        objs = objs.map { ob ->
//            val a2 = ObjUtils.makeVertexIndexed(ob)
            val a3 = ObjUtils.triangulate(ob)
            val a4 = ObjUtils.makeNormalsUnique(a3)
            val a5 = ObjUtils.convertToRenderable(a4)
            return@map a5
        }



        createProjectionMatrix(width, height)
        createViewMatrix()


        val r4 = ObjData.getVerticesArray(objs[0])
        r5 = ObjData.getTotalNumFaceVertices(objs[0])
//        val r71 = ObjData.getNormals(obj)
//        val r72 = ObjData.getNormalsArray(objs[0])
//        val r73 = ObjData.getFaceNormalIndicesArray(obj)
        val points = toVertexList(r4)
//        val normals = toVerticList(r72)
        val finalVertices = mutableListOf<Float>()
        val finalNormals = mutableListOf<Float>()
        val faces = mutableListOf<Face>()
        val triangleWithIndices = mutableListOf<TriangleWithIndices>()
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

            // Сохраняем треугольники с индексами вершин для вычисления нормалей к вершинам
            triangleWithIndices += TriangleWithIndices(indices = l)


            faces += Face(l)
        }

        figure.calculateVertexNormals(points)
        figure.tessellatePN(tessellationLevel = 1)

        val r10 = figure.toVertexList().toFloatArray()
        r5 = r10.count()
        r11 = ByteBuffer.allocateDirect(r10.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        r11?.put(r10)

//        val r123 = ObjData.getNormalsArray(obj)
        val r12 = figure.toNormalList().toFloatArray()
        r13 = ByteBuffer.allocateDirect(r12.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        r13?.put(r12)

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

        b = ByteBuffer.allocateDirect(colors.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        b?.put(colors)
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

    data class EyePosition(
        val eyeX: Float = 0f, val eyeY: Float = 3f, val eyeZ: Float = 7f
    )

    private var eyePosition = EyePosition()
    private var scaleFactor: Float = 1f

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
        val a = 0
    }

    fun onScrollEvent(dx: Float, dy: Float) {

        eyePosition = EyePosition(
            eyeX = eyePosition.eyeX + dx / 10,
            eyeY = eyePosition.eyeY + dy / 10,
            eyeZ = 0f
        )

        setModelMatrix()
    }

    fun onSmoothingLevelChanged(smoothingLevel: Int) {
        figure.tessellatePN(tessellationLevel = smoothingLevel)
        val r10 = figure.toVertexList().toFloatArray()
        r5 = r10.count()
        val r12 = figure.toNormalList().toFloatArray()

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

        renderFrame2()
    }

    fun onScaleEvent(scaleFactor: Float) {
        this.scaleFactor = scaleFactor
        setModelMatrix()
    }

    private var renderObject: RenderObject? = null

    private fun setModelMatrix() {
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.rotateM(modelMatrix, 0, eyePosition.eyeY, 1f, 0f, 0f)
        Matrix.rotateM(modelMatrix, 0, eyePosition.eyeX, 0f, 1f, 0f)
        Matrix.scaleM(modelMatrix, 0, scaleFactor, scaleFactor, scaleFactor)
    }

    var angle = 0f

    fun renderFrame(obj: Obj) {
        GLES32.glDisable(GLES32.GL_DEPTH_TEST)
//        val indices: IntBuffer = ObjData.getFaceVertexIndices(obj)
//        val vertices = ObjData.getVertices(obj)


//        val r1 = ObjData.getFaceNormalIndices(obj)
        val r2 = ObjData.getFaceVertexIndices(obj)
//        val r3 = ObjData.getNormalsArray(obj)
        val r4 = ObjData.getVerticesArray(obj)
        val r5 = ObjData.getTotalNumFaceVertices(obj)
//        val r6 = ObjData.convertToShortBuffer(r2)
//        val r7 = ObjData.getFaceNormalIndicesArray(obj)
//        val r8 = ObjData.getFaceVertexIndicesArray(obj)
//        val points = toVerticList(r4)
        val finalVertices = mutableListOf<Float>()
        val faces = mutableListOf<Face>()

//        val r101 = ObjData.getNormalsArray(obj)
//        val r102 = ObjData.getFaceNormalIndicesArray(obj)

        // Normals
//        for (i in 0 until obj.numNormals) {
//            val face = obj.getNormal(i)
//            val l = mutableListOf<Int>()
//            for (z in 0 until face.) {
//                val vert = face.getVertexIndex(z)
//                finalVertices += points[vert].x
//                finalVertices += points[vert].y
//                finalVertices += points[vert].z
//                l += vert
//            }
//
//            faces += Face(l)
//        }

        // Перенести в линии!! TODO
        // Faces
//        for (i in 0 until obj.numFaces) {
//            val face = obj.getFace(i)
//            val l = mutableListOf<Int>()
//            val vs = mutableListOf<Vertic>()
//            for (z in 0 until face.numVertices) {
////                face.getNormalIndex()
//                val vert = face.getVertexIndex(z)
//                val v = Vertic(
//                    points[vert].x,
//                    points[vert].y,
//                    points[vert].z
//                )
////                finalVertices += points[vert].x
////                finalVertices += points[vert].y
////                finalVertices += points[vert].z
//                l += vert
//                vs += v
//            }
//            finalVertices += vs[0].toList()
//            finalVertices += vs[1].toList()
//            finalVertices += vs[1].toList()
//            finalVertices += vs[2].toList()
//            finalVertices += vs[2].toList()
//            finalVertices += vs[1].toList()
//
//            faces += Face(l)
//        }

        val r10 = finalVertices.toFloatArray()
        val r11 = ByteBuffer.allocateDirect(r10.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        r11.put(r10)

        val colors = FloatArray(r5 * 3)
        for (i in 0 until r5 * 3) {
            colors[i] = color[i % color.size]
        }

        b = ByteBuffer.allocateDirect(colors.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        b?.put(colors)


        val vPMatrix = FloatArray(16)
        Matrix.multiplyMM(vPMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, vPMatrix, 0)

        GLES32.glEnableVertexAttribArray(vertexLocation)
        GLES32.glVertexAttribPointer(vertexLocation, 3, GLES32.GL_FLOAT, false, 0, r11.position(0))

        GLES32.glGetUniformLocation(mProgram, "uMVPMatrix").also { matrix ->
            GLES32.glUniformMatrix4fv(matrix, 1, false, vPMatrix, 0)
        }


        GLES32.glDrawArrays(GLES32.GL_LINES, 0, r10.count() / 3)

        val aw10 = GLES32.glGetError()
        val qewqwr = 0
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

        val aw10 = GLES32.glGetError()
        val qewqwr = 0
    }

    fun renderFrame() {
        GLES32.glClearColor(0.5f, 0.5f, 0.5f, 1.0f)
        GLES32.glClear(GLES32.GL_DEPTH_BUFFER_BIT or GLES32.GL_COLOR_BUFFER_BIT)
        setModelMatrix()


//        GLES32.glLineWidth(15f)
//        GLES32.glLineWidth(1f)
        objs.forEach { renderFrame2() }
//        objs.forEach(::renderFrame)

//        angle += 1
//        if (angle > 360) {
//            angle -= 360
//        }
        val aw = GLES32.glGetError()
        val be = 0

        GLES32.glFlush()
    }
}

data class Vertic(
    val x: Float,
    val y: Float,
    val z: Float
) {
    fun toList(): List<Float> = listOf(x, y, z)

    operator fun minus(v2: Vertic): Vertic {
        return Vertic(
            x = this.x - v2.x,
            y = this.y - v2.y,
            z = this.z - v2.z
        )
    }
}

fun crossProduct(v1: Vertic, v2: Vertic): Vertic {
    return Vertic(
        x = v1.y * v2.z - v1.z * v2.y,
        y = v1.z * v2.x - v1.x * v2.z,
        z = v1.x * v2.y - v1.y * v2.x
    )
}

data class Face(
    val list: List<Int>
)

fun toVertexList(array: FloatArray): List<Vertex> {
    val vertices = mutableListOf<Vertex>()
    for (i in 0 until array.count() - 2 step 3) {
        val v = Vertex(
            x = array[i],
            y = array[i + 1],
            z = array[i + 2]
        )
        vertices += v
    }

    return vertices
}