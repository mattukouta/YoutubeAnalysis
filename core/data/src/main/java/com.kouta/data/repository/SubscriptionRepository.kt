package com.kouta.data.repository

import androidx.paging.LoadType
import com.kouta.data.YoutubeServiceImpl
import com.kouta.data.apiConnect
import com.kouta.data.vo.ApiResponse
import com.kouta.data.vo.ApiResponse.Error.ParseException.onError
import com.kouta.data.vo.ApiResponse.Error.ParseException.onSuccess
import com.kouta.data.vo.dao.SubscriptionDao
import com.kouta.data.vo.entity.SubscriptionEntity
import com.kouta.data.vo.subscriptions.Subscriptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SubscriptionRepository @Inject constructor(
    private val service: YoutubeServiceImpl,
    private val dao: SubscriptionDao
) {
    private val _totalResultsAvailable: MutableStateFlow<Int?> = MutableStateFlow(null)
    val totalResultsAvailable = _totalResultsAvailable.asStateFlow()

    private suspend fun insert(subscriptionEntities: List<SubscriptionEntity>) =
        dao.insertSubscriptions(*subscriptionEntities.toTypedArray())

    fun get() = dao.getSubscriptions()
    suspend fun delete(subscriptionEntities: List<SubscriptionEntity>) =
        dao.deleteSubscriptions(*subscriptionEntities.toTypedArray())

    private suspend fun deleteAll() = dao.deleteAllSubscription()

    suspend fun getSubscriptions(
        query: Subscriptions.RequestQuery,
        loadType: LoadType
    ): ApiResponse<Subscriptions.Response> = apiConnect {
        service.getSubscriptions(query.toQueryMap())
    }.onSuccess {
        if (query.pageToken.isNullOrEmpty()) _totalResultsAvailable.emit(it.pageInfo.totalResults)

        val subscriptionEntities = it.items.map { item ->
            item.snippet?.let { snippet ->
                SubscriptionEntity(
                    id = snippet.resourceId.channelId,
                    title = snippet.title,
                    imageUrl = snippet.thumbnails.default.url
                )
            }
        }

        if (loadType == LoadType.REFRESH) {
            deleteAll()
        }

        insert(subscriptionEntities.filterNotNull())
    }.onError {
        deleteAll()
    }
}