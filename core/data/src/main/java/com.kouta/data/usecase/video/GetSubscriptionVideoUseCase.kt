package com.kouta.data.usecase.video

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.cachedIn
import com.kouta.data.enums.LiveBroadcastContent
import com.kouta.data.enums.RefreshType
import com.kouta.data.enums.RefreshType.*
import com.kouta.data.repository.FeedRepository
import com.kouta.data.repository.SubscriptionRepository
import com.kouta.data.repository.SubscriptionVideoRepository
import com.kouta.data.repository.VideoRepository
import com.kouta.data.vo.ApiResponse
import com.kouta.data.vo.entity.SubscriptionVideo
import com.kouta.data.vo.feeds.Feeds
import com.kouta.data.vo.subscriptions.Subscriptions
import com.kouta.data.vo.video.Video
import com.kouta.data.vo.video.Video.RequestQuery.Filter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cache
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class GetSubscriptionVideoUseCase @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val feedRepository: FeedRepository,
    private val videoRepository: VideoRepository,
    private val subscriptionVideoRepository: SubscriptionVideoRepository
) {
    companion object {
        const val DATABASE_SIZE = 5
    }

    fun getTotalResultsAvailable() = subscriptionVideoRepository.totalResultsAvailable

    fun get(
        scope: CoroutineScope,
        filter: LiveBroadcastContent
    ): Flow<PagingData<SubscriptionVideo>> = Pager(
            config = PagingConfig(
                pageSize = DATABASE_SIZE,
                initialLoadSize = DATABASE_SIZE
            ),
            pagingSourceFactory = {
                subscriptionVideoRepository.loadSubscriptionVideos(filter)
            },
            remoteMediator = SubscriptionVideoMediator(
                scope = scope,
                subscriptionQuery = Subscriptions.RequestQuery(
                    part = listOf(
                        Subscriptions.RequestQuery.Part.SNIPPET
                    ),
                    filter = Subscriptions.RequestQuery.Filter.Mine,
                    forChannelId = listOf(),
                    maxResults = DATABASE_SIZE,
                    order = Subscriptions.RequestQuery.Order.RELEVANCE,
                    pageToken = null

                ),
                videoQuery = Video.RequestQuery(
                    parts = listOf(
                        Video.RequestQuery.Part.ID,
                        Video.RequestQuery.Part.SNIPPET,
                        Video.RequestQuery.Part.LIVE_STREAMING_DETAILS
                    ),
                    filter = Filter.Id(listOf()),
                    maxResults = DATABASE_SIZE,
                    pageToken = null
                ),
                getSubscriptions = { query, loadType ->
                    subscriptionRepository.getSubscriptions(
                        query = query,
                        loadType = loadType
                    )
                },
                getFeeds = { channelId ->
                    feedRepository.getFeeds(channelId)
                },
                getVideos = { query, loadType ->
                    videoRepository.get(query, loadType)
                }
            )

    ).flow.cachedIn(scope)
}

@OptIn(ExperimentalPagingApi::class)
class SubscriptionVideoMediator(
    private val scope: CoroutineScope,
    private var subscriptionQuery: Subscriptions.RequestQuery,
    private var videoQuery: Video.RequestQuery,
    private val getSubscriptions: suspend (query: Subscriptions.RequestQuery, loadType: LoadType) -> ApiResponse<Subscriptions.Response>,
    private val getFeeds: suspend (channelId: String) -> ApiResponse<Feeds.Response>,
    private val getVideos: suspend (query: Video.RequestQuery, loadType: LoadType) -> ApiResponse<Video.Response>,
) : RemoteMediator<Int, SubscriptionVideo>() {
    private var isInitialLoad = true

    private var videoIdList: List<List<String>> = mutableListOf()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SubscriptionVideo>
    ): MediatorResult = withContext(scope.coroutineContext + Dispatchers.IO) {
        Timber.d("ktakamat: load")
        try {
            if (loadType == LoadType.PREPEND) return@withContext MediatorResult.Success(true)

            if (loadType == LoadType.REFRESH) {
                subscriptionQuery = subscriptionQuery.copy(pageToken = null)
                videoQuery = videoQuery.copy(pageToken = null)
            }

            when {
                subscriptionQuery.pageToken.isNullOrEmpty() && videoIdList.isEmpty() && isInitialLoad -> {
                    Timber.d("ktakamat:request case1")
                    // 2つのpageTokenがnullの場合は、subscriptionを取得

                    requestSubscription(loadType)
                }

                !subscriptionQuery.pageToken.isNullOrEmpty() && videoIdList.isEmpty() -> {
                    Timber.d("ktakamat:request case3")

                    requestSubscription(loadType)
                }

                !subscriptionQuery.pageToken.isNullOrEmpty() && videoIdList.isNotEmpty() -> {
                    Timber.d("ktakamat:request case4")
                    // 2つのpageTokenがnullじゃない場合は、videoを取得

                    requestVideos(loadType)
                }

                subscriptionQuery.pageToken.isNullOrEmpty() && videoIdList.isNotEmpty() -> {
                    Timber.d("ktakamat:request case5")

                    requestVideos(loadType)
                }

                else -> {
                    Timber.d("ktakamat:request case error")
                    MediatorResult.Error(Exception())
                }

            }

        } catch (e: IOException) {
            Timber.d("ktakamat:request catch=${e.message}")
            MediatorResult.Error(e)
        }
    }

    private suspend fun requestSubscription(
        loadType: LoadType
    ) = when (val response = getSubscriptions(subscriptionQuery, loadType)) {
        is ApiResponse.Success -> {
            subscriptionQuery =
                subscriptionQuery.copy(pageToken = response.data.nextPageToken)

            videoIdList = response.data.items.mapNotNull {
                it.snippet?.resourceId?.channelId?.let { channelId ->
                    when (val feedsResponse = getFeeds(channelId)) {
                        is ApiResponse.Success -> feedsResponse.data.entry.map { entry -> entry.videoId }
                        is ApiResponse.Error -> listOf()
                    }
                }
            }

            if (!isInitialLoad) {
                requestVideos(loadType)
            } else {
                isInitialLoad = false
                MediatorResult.Success(false)
            }
        }

        is ApiResponse.Error -> MediatorResult.Error(Exception())
    }

    private suspend fun requestVideos(loadType: LoadType): MediatorResult {
        val results = videoIdList.map {
            getVideos(videoQuery.copy(filter = Filter.Id(it), maxResults = it.size), loadType)
        }

        val success = results.filterIsInstance<ApiResponse.Success<Video.Response>>()
        val error = results.filterIsInstance<ApiResponse.Error>()

        return when {
            success.isNotEmpty() -> {
                videoIdList = listOf()
                MediatorResult.Success(subscriptionQuery.pageToken == null)
            }

            error.isEmpty() -> {
                videoIdList = listOf()
                MediatorResult.Success(subscriptionQuery.pageToken == null)
            }
            else -> MediatorResult.Error(Exception())
        }
    }
}