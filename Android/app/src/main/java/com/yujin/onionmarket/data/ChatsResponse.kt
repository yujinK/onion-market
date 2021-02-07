package com.yujin.onionmarket.data

import com.google.gson.annotations.SerializedName

data class ChatsResponse(
    @SerializedName("chats")
    var chats: ArrayList<Chat>
)