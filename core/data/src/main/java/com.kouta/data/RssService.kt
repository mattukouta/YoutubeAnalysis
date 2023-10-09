package com.kouta.data

import com.kouta.data.vo.feeds.Feeds
import retrofit2.http.GET
import retrofit2.http.Query

interface RssService {
    @GET("feeds/videos.xml")
    suspend fun getChannelRssFromChannelId(@Query("channel_id") channelId: String): Feeds.Response
}