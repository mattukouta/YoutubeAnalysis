package com.kouta.data.repository

import com.kouta.data.RssService
import com.kouta.data.apiConnect
import javax.inject.Inject

class FeedRepository @Inject constructor(
    private val service: RssService
) {
    suspend fun getFeeds(channelId: String) = apiConnect {
        service.getChannelRssFromChannelId(channelId)
    }
}