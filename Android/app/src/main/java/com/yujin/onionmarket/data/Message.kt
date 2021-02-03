package com.yujin.onionmarket.data

import com.google.gson.annotations.SerializedName

data class Message (
    @SerializedName("id")
    var id: Int = -1,

    @SerializedName("message")
    var message: String = "",

    @SerializedName("createdAt")
    var createdAt: String = "",

    @SerializedName("User")
    var user: User,

    @SerializedName("chatId")
    var chatId: Int = -1
)