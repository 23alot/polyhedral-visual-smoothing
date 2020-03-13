package android.content

import java.io.BufferedReader
import java.io.InputStreamReader

fun Context.readTextFromRaw(resId: Int): String {
    val stringBuilder = StringBuilder()
    runCatching {
        InputStreamReader(resources.openRawResource(resId)).use { streamReader ->
            BufferedReader(streamReader).use { bufferedReader ->
                bufferedReader.forEachLine { line ->
                    stringBuilder.append(line).append("\r\n")
                }
            }

        }
    }.onFailure { throwable -> throwable.printStackTrace() }

    return stringBuilder.toString()
}