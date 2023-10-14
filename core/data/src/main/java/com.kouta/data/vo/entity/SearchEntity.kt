package com.kouta.data.vo.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kouta.data.vo.search.Search

@Entity
data class SearchVideoEntity(
    @PrimaryKey
    val videoId: String,
    val publishedAt: String,
    val channelId: String,
    val title: String,
    val description: String,
    val thumbnails: Search.Response.SearchItems.Snippet.Thumbnails,
    val channelTitle: String
)
