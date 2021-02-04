package com.yujin.onionmarket.data

data class SendMessage (
    var chatId: Int,
    var nick: String,
    var profile: String,
    var message: String,
    var createdAt: String
)