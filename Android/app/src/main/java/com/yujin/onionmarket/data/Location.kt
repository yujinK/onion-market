package com.yujin.onionmarket.data

import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("id")
    var id: Int = -1,

    @SerializedName("sido")
    var sido: String = "",

    @SerializedName("sigun")
    var sigun: String = "",

    @SerializedName("dongmyeon")
    var dongmyeon: String = "",

    @SerializedName("li")
    var li: String = ""
)