package com.udhaarpay.app.data.model

data class MarketNewsItem(
    val title: String,
    val source: String,
    val link: String,
    val publishedAtMillis: Long,
    val imageUrl: String? = null,
    val summary: String? = null
)
