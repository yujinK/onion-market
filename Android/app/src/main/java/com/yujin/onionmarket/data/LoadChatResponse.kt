package com.yujin.onionmarket.data

import com.google.gson.annotations.SerializedName

data class LoadChatResponse (
    @SerializedName("messages")
    var messages: ArrayList<Message>
)