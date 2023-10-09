package com.kouta.data.repository

import androidx.paging.LoadType
import com.kouta.data.YoutubeServiceImpl
import com.kouta.data.apiConnect
import com.kouta.data.vo.ApiResponse.Error.ParseException.onError
import com.kouta.data.vo.ApiResponse.Error.ParseException.onSuccess
import com.kouta.data.vo.Thumbnail
import com.kouta.data.vo.dao.SearchVideoDao
import com.kouta.data.vo.entity.SearchVideoEntity
import com.kouta.data.vo.search.Search
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val service: YoutubeServiceImpl,
    private val searchVideoDao: SearchVideoDao
) {
    suspend fun insertSearchVideos(searchVideos: List<SearchVideoEntity>) =
        searchVideoDao.insert(*searchVideos.toTypedArray())

    fun getSearchVideos() = searchVideoDao.get()
    suspend fun deleteSearchVideo(searchVideo: SearchVideoEntity) =
        searchVideoDao.delete(searchVideo)

    suspend fun deleteAllSearchVideos() = searchVideoDao.deleteAll()

    suspend fun getSearch(queryMap: Map<String, String>, loadType: LoadType) = apiConnect {
        service.getSearch(queryMap)
    }.onSuccess {
        val searchVideos = it.items.mapNotNull { item ->
            item.id.channelId?.let { channelId ->
                val snippet = item.snippet
                val thumbnails = snippet.thumbnails
                SearchVideoEntity(
                    videoId = channelId,
                    publishedAt = snippet.publishedAt,
                    channelId = snippet.channelId,
                    title = snippet.title,
                    description = snippet.description,
                    thumbnails = Search.Response.SearchItems.Snippet.Thumbnails(
                        default = Thumbnail(
                            url = thumbnails.default.url,
                            width = thumbnails.default.width,
                            height = thumbnails.default.height
                        ),
                        medium = Thumbnail(
                            url = thumbnails.medium.url,
                            width = thumbnails.medium.width,
                            height = thumbnails.medium.height
                        ),
                        high = Thumbnail(
                            url = thumbnails.high.url,
                            width = thumbnails.high.width,
                            height = thumbnails.high.height
                        ),
                        standard = Thumbnail(
                            url = thumbnails.standard.url,
                            width = thumbnails.standard.width,
                            height = thumbnails.standard.height
                        ),
                        maxres = Thumbnail(
                            url = thumbnails.maxres.url,
                            width = thumbnails.maxres.width,
                            height = thumbnails.maxres.height
                        )
                    ),
                    channelTitle = snippet.channelTitle

                )
            }
        }

        if (loadType == LoadType.REFRESH) {
            deleteAllSearchVideos()
        }

        insertSearchVideos(searchVideos)
    }.onError {
        deleteAllSearchVideos()
    }
}