package com.yujin.onionmarket.data

data class Message (
    var id: Int,
    var message: String,
    var createdAt: String,
    var profileImg: String,
    var nick: String,
    var chatId: Int
)