package com.kouta.data

import com.kouta.auth.AuthRepository
import com.kouta.data.vo.ApiResponse
import com.kouta.data.vo.activities.ChannelActivities
import com.kouta.data.vo.channels.Channels
import com.kouta.data.vo.search.Search
import com.kouta.data.vo.subscriptions.Subscriptions
import com.kouta.data.vo.video.Video
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.http.GET
import retrofit2.http.QueryMap
import timber.log.Timber
import javax.inject.Inject

interface YoutubeService {
    @GET("channels")
    suspend fun getChannels(@QueryMap queryMap: Map<String, String>): Channels.Response

    @GET("activities")
    suspend fun getChannelActivities(
        @QueryMap queryMap: Map<String, String>
    ): ChannelActivities.Response

    @GET("subscriptions")
    suspend fun getSubscriptions(@QueryMap queryMap: Map<String, String>): Subscriptions.Response

    @GET("search")
    suspend fun getSearch(@QueryMap queryMap: Map<String, String>): Search.Response

    @GET("videos")
    suspend fun getVideos(@QueryMap queryMap: Map<String, String>): Video.Response
}

class YoutubeServiceImpl @Inject constructor(
    private val service: YoutubeService,
    private val authRepository: AuthRepository
) : YoutubeService {
    override suspend fun getChannels(queryMap: Map<String, String>): Channels.Response =
        service.getChannels(queryMap.setAccessToken())

    override suspend fun getChannelActivities(queryMap: Map<String, String>): ChannelActivities.Response =
        service.getChannelActivities(queryMap.setAccessToken())

    override suspend fun getSubscriptions(queryMap: Map<String, String>): Subscriptions.Response =
        service.getSubscriptions(queryMap.setAccessToken())

    override suspend fun getSearch(queryMap: Map<String, String>): Search.Response =
        service.getSearch(queryMap.setAccessToken())

    override suspend fun getVideos(queryMap: Map<String, String>): Video.Response =
        service.getVideos(queryMap.setAccessToken())


    private suspend fun Map<String, String>.setAccessToken(): Map<String, String> {
        authRepository.requestAccessToken()?.let {
            this.toMutableMap().apply {
                this += ("access_token" to it)
            }.also {
                return it
            }
        }

        return this
    }
}

suspend fun <T> apiConnect(action: suspend () -> T): ApiResponse<T> = withContext(Dispatchers.IO) {
    try {
        ApiResponse.Success(action())
    } catch (e: HttpException) {
        Timber.d("ktakamat:apiConnect:httpException=${e.message}")
        ApiResponse.Error.Default(e.message() + "\n" + e.stackTraceToString())
    } catch (e: JsonDataException) {
        Timber.d("ktakamat:apiConnect:jsonDataException=${e.message}")
        ApiResponse.Error.ParseException
    } catch (e: Exception) {
        Timber.d("ktakamat:apiConnect:exception=${e.message}")
        ApiResponse.Error.Default(e.message ?: e.stackTrace.toString())
    }
}
