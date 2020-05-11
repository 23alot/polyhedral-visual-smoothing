package com.supesuba.utils.common

fun String.fileName(): String {
    val a = this.lastIndexOf("/")
    return this.substring(a + 1)
}