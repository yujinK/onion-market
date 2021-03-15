package com.yujin.onionmarket.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Image(
    @SerializedName("id")
    var id: Int = -1,

    @SerializedName("path")
    var path: String = "",

    @SerializedName("priority")
    var priority: Int = 0
) : Parcelable