package com.kouta.data.vo.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kouta.data.enums.LiveBroadcastContent
import com.kouta.data.vo.video.Video

@Entity
data class VideoEntity(
    @PrimaryKey
    val videoId: String,
    val index: Int,
    @Embedded
    val snippet: Snippet?,
    @Embedded
    val liveStreamingDetails: Video.Response.Video.LiveStreamingDetails?
) {
    data class Snippet(
        val publishedAt: String,
        val channelId: String,
        val title: String,
        val description: String,
        @Embedded
        val thumbnails: Video.Response.Video.Snippet.Thumbnails,
        val channelTitle: String,
        val liveBroadcastContent: LiveBroadcastContent
    )
}
