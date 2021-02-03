package com.yujin.onionmarket.data

import com.google.gson.annotations.SerializedName

data class ReadChatResponse (
    @SerializedName("messages")
    var messages: ArrayList<Message>
)