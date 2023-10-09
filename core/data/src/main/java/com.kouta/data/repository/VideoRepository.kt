package com.kouta.data.repository

import androidx.paging.LoadType
import com.kouta.data.YoutubeServiceImpl
import com.kouta.data.apiConnect
import com.kouta.data.enums.LiveBroadcastContent
import com.kouta.data.vo.ApiResponse.Error.ParseException.onSuccess
import com.kouta.data.vo.dao.SubscriptionDao
import com.kouta.data.vo.entity.VideoEntity
import com.kouta.data.vo.video.Video
import javax.inject.Inject

class VideoRepository @Inject constructor(
    private val apiService: YoutubeServiceImpl,
    private val subscriptionDao: SubscriptionDao
) {

    private suspend fun insertVideo(videos: List<VideoEntity>) =
        subscriptionDao.insertVideos(*videos.toTypedArray())

    private suspend fun deleteAll() = subscriptionDao.deleteAllSubscription()

    fun loadSubscriptionVideos(liveBroadcastContent: LiveBroadcastContent) =
        if (liveBroadcastContent == LiveBroadcastContent.UNKNOWN) {
            subscriptionDao.getSubscriptionVideoAll()
        } else {
            subscriptionDao.getSubscriptionVideoFilterLiveBroadcastContent(liveBroadcastContent)
        }


    private var index = 0
    suspend fun get(
        query: Video.RequestQuery,
        loadType: LoadType
    ) = apiConnect {
        apiService.getVideos(query.toQueryMap())
    }.onSuccess {

        if (loadType == LoadType.REFRESH) {
            deleteAll()
            index = 0
        }

        val videos = it.items.mapNotNull { video ->
            video.id?.let { videoId ->
                index += 1

                VideoEntity(
                    videoId = videoId,
                    index = index,
                    snippet = video.snippet?.let { snippet ->
                        VideoEntity.Snippet(
                            publishedAt = snippet.publishedAt,
                            channelId = snippet.channelId,
                            title = snippet.title,
                            description = snippet.description,
                            thumbnails = snippet.thumbnails,
                            channelTitle = snippet.channelTitle,
                            liveBroadcastContent = snippet.liveBroadcastContent
                        )
                    },
                    liveStreamingDetails = video.liveStreamingDetails
                )
            }
        }

        insertVideo(videos)
    }
}