package com.supesuba.smoothing.trash

import java.nio.FloatBuffer
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/**
 * Created by 23alot on 15.03.2020.
 * TODO: обобщить до nxn
 */
fun identityMatrix(): Array<Float> = arrayOf(
    1f, 0f, 0f, 0f,
    0f, 1f, 0f, 0f,
    0f, 0f, 1f, 0f,
    0f, 0f, 0f, 1f
)

fun <T: Number> Matrix<T>.translateMatrix(x: T, y: T, z: T): Matrix<T> {
    val identityMatrix = Matrix.identityMatrix<T>(4)
    identityMatrix.matrix[12] = x
    identityMatrix.matrix[13] = y
    identityMatrix.matrix[14] = z
    return this.matrixMultiply(identityMatrix)
}

fun <T: Number> Matrix<T>.matrixScale(x: T, y: T, z: T): Matrix<T> {
    val identityMatrix = Matrix.identityMatrix<T>(4)
    identityMatrix.matrix[0] = x
    identityMatrix.matrix[5] = y
    identityMatrix.matrix[10] = z
    return this.matrixMultiply(identityMatrix)
}

fun <T: Number> Matrix<T>.matrixRotateX(angle: Float): Matrix<T> {
    val identityMatrix = Matrix.identityMatrix<T>(4)
    val rad = toRadians(angle)
    identityMatrix.matrix[5] = cos(rad) as T
    identityMatrix.matrix[9] = -sin(rad) as T
    identityMatrix.matrix[6] = sin(rad) as T
    identityMatrix.matrix[10] = cos(rad) as T
    return this.matrixMultiply(identityMatrix)
}

fun <T: Number> Matrix<T>.matrixRotateY(angle: Float): Matrix<T> {
    val identityMatrix = Matrix.identityMatrix<T>(4)
    val rad = toRadians(angle)
    identityMatrix.matrix[0] = cos(rad) as T
    identityMatrix.matrix[8] = sin(rad) as T
    identityMatrix.matrix[2] = -sin(rad) as T
    identityMatrix.matrix[10] = cos(rad) as T
    return this.matrixMultiply(identityMatrix)
}

fun <T: Number> Matrix<T>.matrixRotateZ(angle: Float): Matrix<T> {
    val identityMatrix = Matrix.identityMatrix<T>(4)
    val rad = toRadians(angle)
    identityMatrix.matrix[0] = cos(rad) as T
    identityMatrix.matrix[4] = -sin(rad) as T
    identityMatrix.matrix[1] = sin(rad) as T
    identityMatrix.matrix[5] = cos(rad) as T
    return this.matrixMultiply(identityMatrix)
}

fun <T: Number> Matrix<T>.matrixPerspective(fieldOfView: Float, aspectRatio: Float, zNear: Float, zFar: Float): Matrix<T> {
    val ymax = zNear * tan(fieldOfView * PI / 360.0).toFloat()
    val xmax = ymax * aspectRatio
    return matrixFrustum(-xmax, xmax, -ymax, ymax, zNear, zFar)
}

fun <T: Number> Matrix<T>.matrixFrustum(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float): Matrix<T> {
    val temp = 2.0 *zNear
    val xDistance = right - left
    val yDistance = top - bottom
    val zDistance = zFar - zNear
    val identityMatrix = Matrix.identityMatrix<T>(4)
    identityMatrix.matrix[0] = (temp / xDistance) as T
    identityMatrix.matrix[5] = (temp / yDistance) as T
    identityMatrix.matrix[8] = ((right + left) / xDistance) as T
    identityMatrix.matrix[9] = ((top + bottom) / yDistance) as T
    identityMatrix.matrix[10] = ((-zFar - zNear) / zDistance) as T
    identityMatrix.matrix[11] = (-1.0f) as T
    identityMatrix.matrix[14] = ((-temp * zFar) / zDistance) as T
    identityMatrix.matrix[15] = (0.0f) as T
    return identityMatrix
}

fun toRadians(angle: Float): Float {
    return angle / 180 * PI.toFloat()
}

fun <T: Number> Matrix<T>.matrixMultiply(other: Matrix<T>): Matrix<T> {
    val result = Matrix.identityMatrix<T>(4)
    for (i in 0..3){
        for (j in 0..3) {

            val a = matrix[j].toFloat() * other.matrix[4 * i].toFloat() +
                    matrix[4 +j].toFloat() * other.matrix[4 * i + 1].toFloat() +
                    matrix[8 + j].toFloat() * other.matrix[4 * i + 2].toFloat() +
                    matrix[12 + j].toFloat() * other.matrix[4 * i + 3].toFloat()
            result.matrix[4 * i + j] = a as T
        }
    }

    return result
}

fun Matrix<Float>.toFloatBuffer(): FloatBuffer = FloatBuffer.wrap(matrix.toFloatArray())