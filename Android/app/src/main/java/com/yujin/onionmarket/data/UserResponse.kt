package com.yujin.onionmarket.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("user")
    var user: List<User>
)

data class User(
    @SerializedName("id")
    @Expose
    var id: Int = 0,

    @SerializedName("email")
    @Expose
    var email: String = "",

    @SerializedName("nick")
    @Expose
    var nick: String = "",

    @SerializedName("img")
    @Expose
    var img: String = "",

    @SerializedName("locationId")
    @Expose
    var locationId: Int = 0
)