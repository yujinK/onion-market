package com.yujin.onionmarket.data

import com.google.gson.annotations.SerializedName

data class UserChatsResponse(
    @SerializedName("chats")
    var chats: ArrayList<UserChat>
)

data class UserChat(
    @SerializedName("id")
    var id: Int = -1,

    @SerializedName("lastMessage")
    var lastMessage: String = "",

    @SerializedName("createdAt")
    var createdAt: String = "",

    @SerializedName("updatedAt")
    var updatedAt: String = "",

    @SerializedName("User")
    var buyUser: User,

    @SerializedName("Sale")
    var sale: Sale,
)