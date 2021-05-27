package com.jschoi.develop.aop_part03_chapter06.home

data class ArticleModel(
    val sellerId: String,
    val title: String,
    val createdAt: Long,
    val price: String,
    val imageUrl: String
) {
    constructor() : this("", "", 0, "", "")
}