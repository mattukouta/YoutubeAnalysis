package com.kouta.data.repository

import com.kouta.data.YoutubeServiceImpl
import com.kouta.data.apiConnect
import com.kouta.data.vo.ApiResponse
import com.kouta.data.vo.ApiResponse.Error.ParseException.onError
import com.kouta.data.vo.ApiResponse.Error.ParseException.onSuccess
import com.kouta.data.vo.activities.ChannelActivities
import com.kouta.data.vo.channels.Channels
import timber.log.Timber
import javax.inject.Inject

class ChannelRepository @Inject constructor(
    private val service: YoutubeServiceImpl
) {
    suspend fun getChannels(queryMap: Map<String, String>): ApiResponse<Channels.Response> = apiConnect {
        Timber.d("ktakamat ${Thread.currentThread().threadGroup}")
        service.getChannels(queryMap)
    }.onSuccess {
        Timber.d("ktakamat getChannels onsuccess")
    }.onError {
        if (it is ApiResponse.Error.Default) {

            Timber.d("ktakamat getChannels onError=${it.message}")
        }
    }

    suspend fun getActivities(queryMap: Map<String, String>): ApiResponse<ChannelActivities.Response> = apiConnect {
        service.getChannelActivities(queryMap)
    }.onSuccess {
        Timber.d("ktakamat getActivities onsuccess")
    }.onError {
        if (it is ApiResponse.Error.Default) {

            Timber.d("ktakamat getActivities onError=${it.message}")
        }
    }
}