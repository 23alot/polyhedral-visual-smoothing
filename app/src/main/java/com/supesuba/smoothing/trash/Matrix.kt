package com.supesuba.smoothing.trash

/**
 * Created by 23alot on 15.03.2020.
 */
class Matrix<T : Number> private constructor() {
    var matrix: MutableList<T> = mutableListOf()
    private set

    private constructor(list: MutableList<T>): this() {
        matrix = list
    }



    companion object {
        fun <T: Number> identityMatrix(numberInRow: Int): Matrix<T> {
            Float
            val tempList = mutableListOf<T>()
            val zero = 0f as T
            val ident = 1f as T

            for (row in 0 until numberInRow) {
                for (column in 0 until numberInRow) {
                    tempList += if (row * numberInRow + column == row * (numberInRow + 1)) {
                        ident
                    } else {
                        zero
                    }
                }
            }

            return Matrix<T>(tempList)
        }
    }
}