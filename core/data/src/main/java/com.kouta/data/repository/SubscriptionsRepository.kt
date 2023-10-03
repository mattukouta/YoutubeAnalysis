package com.kouta.data.repository

import com.kouta.data.YoutubeServiceImpl
import com.kouta.data.apiConnect
import com.kouta.data.vo.ApiResponse
import com.kouta.data.vo.ApiResponse.Error.ParseException.onError
import com.kouta.data.vo.ApiResponse.Error.ParseException.onSuccess
import com.kouta.data.vo.dao.SubscriptionDao
import com.kouta.data.vo.entity.SubscriptionEntity
import com.kouta.data.vo.subscriptions.Subscriptions
import timber.log.Timber
import javax.inject.Inject

class SubscriptionsRepository @Inject constructor(
    private val service: YoutubeServiceImpl,
    private val dao: SubscriptionDao
) {
    suspend fun insert(subscriptionEntities: List<SubscriptionEntity>) = dao.insert(*subscriptionEntities.toTypedArray())
    fun get() = dao.get()
    suspend fun delete(subscriptionEntities: List<SubscriptionEntity>) = dao.delete(*subscriptionEntities.toTypedArray())
    suspend fun deleteAll() = dao.deleteAll()

    suspend fun getSubscriptions(queryMap: Map<String, String>): ApiResponse<Subscriptions.Response> = apiConnect {
        service.getSubscriptions(queryMap)
    }.onSuccess {
        val subscriptionEntities = it.items.map { item ->
            item.snippet?.let { snippet ->
                SubscriptionEntity(
                    id = snippet.resourceId.channelId,
                    title = snippet.title,
                    imageUrl = snippet.thumbnails.default.url
                )
            }
        }

        insert(subscriptionEntities.filterNotNull())
    }.onError {
        deleteAll()
    }
}