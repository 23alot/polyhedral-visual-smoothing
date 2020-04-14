package com.supesuba.smoothing.presentation.view

import android.opengl.GLES20
import android.opengl.GLES32
import android.opengl.Matrix
import android.os.SystemClock
import com.supesuba.smoothing.R
import com.supesuba.smoothing.model.repository.ShaderRepository
import de.javagl.obj.Obj
import de.javagl.obj.ObjData
import de.javagl.obj.ObjSplitting
import de.javagl.obj.ObjUtils
import kotlinx.coroutines.runBlocking
import java.nio.*


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

val cubeVertices = floatArrayOf(-1.0f,  1.0f, -1.0f, /* Back. */
    1.0f,  1.0f, -1.0f,
    -1.0f, -1.0f, -1.0f,
    1.0f, -1.0f, -1.0f,
    -1.0f,  1.0f,  1.0f, /* Front. */
    1.0f,  1.0f,  1.0f,
    -1.0f, -1.0f,  1.0f,
    1.0f, -1.0f,  1.0f,
    -1.0f,  1.0f, -1.0f, /* Left. */
    -1.0f, -1.0f, -1.0f,
    -1.0f, -1.0f,  1.0f,
    -1.0f,  1.0f,  1.0f,
    1.0f,  1.0f, -1.0f, /* Right. */
    1.0f, -1.0f, -1.0f,
    1.0f, -1.0f,  1.0f,
    1.0f,  1.0f,  1.0f,
    -1.0f, -1.0f, -1.0f, /* Top. */
    -1.0f, -1.0f,  1.0f,
    1.0f, -1.0f,  1.0f,
    1.0f, -1.0f, -1.0f,
    -1.0f,  1.0f, -1.0f, /* Bottom. */
    -1.0f,  1.0f,  1.0f,
    1.0f,  1.0f,  1.0f,
    1.0f,  1.0f, -1.0f)

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

val indices = shortArrayOf(0, 2, 3, 0, 1, 3, 4, 6, 7, 4, 5, 7, 8, 9,
    10, 11, 8, 10, 12, 13, 14, 15, 12, 14, 16, 17, 18, 16, 19,
    18, 20, 21, 22, 20, 23, 22)


class Triangle(private val shaderRepository: ShaderRepository) {

    // Set color with red, green, blue and alpha (opacity) values
    val color = floatArrayOf(0.63671875f, 0.26953125f, 0.22265625f, 1f)

    private var mProgram: Int = 0

    fun init2() {

        val vertexShader: Int = runBlocking { shaderRepository.loadShader(GLES20.GL_VERTEX_SHADER, R.raw.vertex_shader) }
        val fragmentShader: Int = runBlocking { shaderRepository.loadShader(GLES20.GL_FRAGMENT_SHADER, R.raw.fragment_shader) }

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram().also {

            // add the vertex shader to program
            GLES20.glAttachShader(it, vertexShader)

            // add the fragment shader to program
            GLES20.glAttachShader(it, fragmentShader)

            // creates OpenGL ES program executables
            GLES20.glLinkProgram(it)
        }
    }

    private var vertexBuffer: FloatBuffer =
        // (number of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(triangleCoords.size * 4).run {
            // use the device hardware's native byte order
            order(ByteOrder.nativeOrder())

            // create a floating point buffer from the ByteBuffer
            asFloatBuffer().apply {
                // add the coordinates to the FloatBuffer
                put(triangleCoords)
                // set the buffer to read the first coordinate
                position(0)
            }
        }

    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0
    private var texture: Int = 0

    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    private var vertexLocation = 0
    private var vertexColourLocation = 0
    private var projectionLocation = 0
    private var modelViewLocation = 0
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)

    var p3: ShortArray = shortArrayOf()

    var a: FloatBuffer? = null
    var b: FloatBuffer? = null
    var c: ShortBuffer? = null
    private lateinit var objs: List<Obj>

    fun setupGraphics(width: Int, height: Int) {
        vertexLocation = GLES20.glGetAttribLocation(mProgram, "vertexPosition")
        vertexColourLocation = GLES20.glGetAttribLocation(mProgram, "vertexColour")
//        GLES20.glEnable(GLES32.GL_DEPTH_TEST)
        GLES20.glViewport(0, 0, width, height)
        GLES20.glUseProgram(mProgram)
        val loader = runBlocking { shaderRepository.test() }
        val p1 = loader.vertices.toFloatArray()
        val p2 = loader.normals
        val po = loader.positions
        val pq = loader.textureCoordinates
        val pw = loader.numFaces
        p3 = po.map { it.toShort() }.toShortArray()
        val obj = runBlocking { shaderRepository.test2() }
//        obj = ObjUtils.makeVertexIndexed(obj)
        objs = ObjSplitting.splitByMaxNumVertices(obj, 65000)
        objs.map { ob ->
            val a2 = ObjUtils.makeVertexIndexed(ob)
            val a3 = ObjUtils.convertToRenderable(ob)
            return@map a3
        }

        val colors = FloatArray(2 * objs[0].numVertices)
        for (i in 0 until 2 * objs[0].numVertices) {
            colors[i] = color[i% color.size]
        }

        b = ByteBuffer.allocateDirect(colors.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        b?.put(colors)

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
        val far = 12f
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

    private fun createViewMatrix() {
        // точка положения камеры
        val eyeX = 2f
        val eyeY = 2f
        val eyeZ = 3f

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

    private fun setModelMatrix() {
        Matrix.setIdentityM(modelMatrix, 0)
        val angle: Float =
            (SystemClock.uptimeMillis() % 10000f) / 10000f * 360f
        Matrix.rotateM(modelMatrix, 0, angle, 0f, 1f, 0f)
        Matrix.scaleM(modelMatrix, 0, 0.5f, 0.5f, 0.5f)
    }

    var angle = 0f

    fun renderFrame(obj: Obj) {
        val indices: IntBuffer = ObjData.getFaceVertexIndices(obj)
        val vertices = ObjData.getVertices(obj)
        val eqwe = ObjData.getVerticesArray(obj)
        val eqwer = obj.numVertices
//        val pq = ObjData.getFaceNormalIndices(obj)
//        val pw = ObjData.getFaceNormalIndicesArray(obj)
//        val pe = ObjData.getNormals(obj)
//
//        val ind = ObjData.convertToShortBuffer(indices)
//
//        val verticesNum = obj.numVertices


        val vPMatrix = FloatArray(16)
        Matrix.multiplyMM(vPMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, vPMatrix, 0)

        GLES20.glEnableVertexAttribArray(vertexLocation)
        vertices.rewind()
        GLES20.glVertexAttribPointer(vertexLocation, 3, GLES20.GL_FLOAT, false, 0, vertices)

        GLES20.glEnableVertexAttribArray(vertexColourLocation)
        b?.rewind()
        GLES20.glVertexAttribPointer(vertexColourLocation, 3, GLES20.GL_FLOAT, false, 0, b)

        GLES20.glGetUniformLocation(mProgram, "uMVPMatrix").also { matrix ->
            GLES20.glUniformMatrix4fv(matrix, 1, false, vPMatrix, 0)
        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, obj.numVertices)

//        GLES20.glDrawElements(GLES20.GL_TRIANGLES, ind?.capacity()?:36, GLES20.GL_UNSIGNED_SHORT, ind)

        val aw10 = GLES20.glGetError()
    }

    fun renderFrame() {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f)
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
        setModelMatrix()

        GLES20.glEnableVertexAttribArray(vertexColourLocation)
        GLES20.glVertexAttribPointer(vertexColourLocation, 3, GLES20.GL_FLOAT, false, 0, b)

        objs.forEach(::renderFrame)

//        angle += 1
//        if (angle > 360) {
//            angle -= 360
//        }
        val aw = GLES20.glGetError()
        val be = 0
    }

    fun draw() {

        // Add program to OpenGL ES environment
        GLES32.glUseProgram(mProgram)
        tutu()
        return

        // get handle to vertex shader's vPosition member
        positionHandle = GLES32.glGetAttribLocation(mProgram, "a_Position").also {

            // Enable a handle to the triangle vertices
            GLES32.glEnableVertexAttribArray(it)

            // Prepare the triangle coordinate data
            GLES32.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES32.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )

            // get handle to fragment shader's vColor member
            // glColor3f нет
            mColorHandle = GLES32.glGetUniformLocation(mProgram, "u_Color").also { colorHandle ->

                // Set color for drawing the triangle
                GLES32.glUniform4fv(colorHandle, 1, color, 0)
            }

            // Draw the triangle
            GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, vertexCount)

            // Disable vertex array
            GLES32.glDisableVertexAttribArray(it)
        }
    }

    fun tutu() {
        val objLoader = runBlocking { shaderRepository.test() }

        val numFaces = objLoader.numFaces

        // Initialize the buffers.
        val positions = ByteBuffer.allocateDirect(objLoader.positions.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        positions.put(objLoader.positions).position(0)

        val vertices = ByteBuffer.allocateDirect(objLoader.vertices.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        vertices.put(objLoader.vertices.toFloatArray()).position(0)

        val normals = ByteBuffer.allocateDirect(objLoader.normals.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        normals.put(objLoader.normals).position(0)

        val textureCoordinates =
            ByteBuffer.allocateDirect(objLoader.textureCoordinates.size * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
        textureCoordinates.put(objLoader.textureCoordinates).position(0)

        // get handle to vertex shader's vPosition member
        positionHandle = GLES32.glGetAttribLocation(mProgram, "a_Position").also {

            // Enable a handle to the triangle vertices
            GLES32.glEnableVertexAttribArray(it)

            // Prepare the triangle coordinate data
            GLES32.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES32.GL_FLOAT,
                false,
                vertexStride,
                vertices
            )

            // get handle to fragment shader's vColor member
            // glColor3f нет
            mColorHandle = GLES32.glGetUniformLocation(mProgram, "u_Color").also { colorHandle ->

                // Set color for drawing the triangle
                GLES32.glUniform4fv(colorHandle, 1, color, 0)
            }

            val vertexNormalLocation = GLES32.glGetAttribLocation(mProgram, "a_VertexNormal").also { normal ->
                GLES32.glVertexAttribPointer(normal, 3, GLES32.GL_FLOAT, false, 0, normals);
                GLES32.glEnableVertexAttribArray(normal);
            }

            // Draw the triangle
            GLES32.glDrawArrays(GLES32.GL_TRIANGLE_FAN, 0, vertices.capacity() / COORDS_PER_VERTEX)

            // Disable vertex array
            GLES32.glDisableVertexAttribArray(it)
        }


    }
}