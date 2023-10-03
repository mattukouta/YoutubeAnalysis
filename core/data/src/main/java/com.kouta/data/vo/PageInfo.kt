package com.kouta.data.vo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PageInfo(
    val resultsPerPage: Int,
    val totalResults: Int
)
