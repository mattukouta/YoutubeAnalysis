package com.kouta.data.vo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Thumbnail(
    val url: String,
    val width: Int?,
    val height: Int?
)