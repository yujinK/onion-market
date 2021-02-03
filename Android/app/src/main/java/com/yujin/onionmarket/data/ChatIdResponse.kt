package com.yujin.onionmarket.data

import com.google.gson.annotations.SerializedName

data class ChatIdResponse (
    @SerializedName("chatId")
    var chatId: Int = -1
)