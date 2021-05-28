package com.jschoi.develop.aop_part03_chapter06.chatdetail

data class ChatItem(
    val senderId: String,
    val message: String
) {
    constructor() : this("", "")
}
