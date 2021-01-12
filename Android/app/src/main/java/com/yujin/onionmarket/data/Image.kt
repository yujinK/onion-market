package com.yujin.onionmarket.data

import com.google.gson.annotations.SerializedName

data class Image(
    @SerializedName("id")
    var id: Int = -1,

    @SerializedName("path")
    var path: String = "",

    @SerializedName("priority")
    var priority: Int = 0
)