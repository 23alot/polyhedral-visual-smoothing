package com.supesuba.smoothing.model.repository

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

interface ModelRepository {

    suspend fun getModels(): List<ModelInfo>
}

@Parcelize
data class ModelInfo(
    val name: String,
    val id: Long,
    val uri: Uri
): Parcelable