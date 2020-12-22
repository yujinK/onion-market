package com.yujin.onionmarket.data

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("user")
    var user: List<User>
)

data class User(
    @SerializedName("email")
    var email: String = "",

    @SerializedName("nick")
    var nick: String = ""
)