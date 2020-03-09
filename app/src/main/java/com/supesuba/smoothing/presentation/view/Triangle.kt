package com.supesuba.smoothing.presentation.view

import android.opengl.GLES32
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

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

class Triangle {


    private val vertexShaderCode =
        "attribute vec4 vPosition;" +
                "void main() {" +
                "  gl_Position = vPosition;" +
                "}"

    private val fragmentShaderCode =
        "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

    // Set color with red, green, blue and alpha (opacity) values
    val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)

    private var mProgram: Int

    init {

        val vertexShader: Int = loadShader(GLES32.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES32.GL_FRAGMENT_SHADER, fragmentShaderCode)

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



    fun loadShader(type: Int, shaderCode: String): Int {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        return GLES32.glCreateShader(type).also { shader ->

            // add the source code to the shader and compile it
            GLES32.glShaderSource(shader, shaderCode)
            GLES32.glCompileShader(shader)
        }
    }

    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0

    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    fun draw() {
        // Add program to OpenGL ES environment
        GLES32.glUseProgram(mProgram)

        // get handle to vertex shader's vPosition member
        positionHandle = GLES32.glGetAttribLocation(mProgram, "vPosition").also {

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
            mColorHandle = GLES32.glGetUniformLocation(mProgram, "vColor").also { colorHandle ->

                // Set color for drawing the triangle
                GLES32.glUniform4fv(colorHandle, 1, color, 0)
            }

            // Draw the triangle
            GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, vertexCount)

            // Disable vertex array
            GLES32.glDisableVertexAttribArray(it)
        }
    }
}