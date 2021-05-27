package com.jschoi.develop.aop_part03_chapter06.chatlist

data class ChatListItem(
    val buyerId: String,
    val sellerId: String,
    val itemTitle: String,
    val key: Long
) {
    // firebase realtime database에서 사용시 기본 생성자가 있어여 한다.
    constructor() : this("", "", "", 0)
}
