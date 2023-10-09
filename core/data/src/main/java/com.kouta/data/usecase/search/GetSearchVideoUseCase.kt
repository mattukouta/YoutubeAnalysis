package com.kouta.data.usecase.search

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.cachedIn
import com.kouta.data.repository.SearchRepository
import com.kouta.data.vo.ApiResponse
import com.kouta.data.vo.entity.SearchVideoEntity
import com.kouta.data.vo.search.Search
import com.kouta.data.vo.search.Search.RequestQuery.Part
import com.kouta.data.vo.search.Search.RequestQuery.Type
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class GetSearchVideoUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    companion object {
        const val DATABASE_SIZE = 5
    }

    @OptIn(ExperimentalPagingApi::class)
    fun get(
        scope: CoroutineScope,
        channelId: String?,
        eventType: Search.RequestQuery.EventType?
    ) = Pager(
        config = PagingConfig(pageSize = DATABASE_SIZE, initialLoadSize = DATABASE_SIZE),
        pagingSourceFactory = { searchRepository.getSearchVideos() },
        remoteMediator = SearchVideoMediator(
            scope = scope,
            query = Search.RequestQuery(
                parts = listOf(Part.ID, Part.SNIPPET),
                channelId = channelId,
                eventType = eventType,
                pageToken = null,
                q = null,
                types = listOf(Type.VIDEO),
            ),
            getItems = { query, loadType ->
                searchRepository.getSearch(query.toQueryMap(), loadType)
            }
        )
    ).flow.cachedIn(scope)
}

@OptIn(ExperimentalPagingApi::class)
class SearchVideoMediator(
    private val scope: CoroutineScope,
    private var query: Search.RequestQuery,
    private val getItems: suspend (Search.RequestQuery, LoadType) -> ApiResponse<Search.Response>
) : RemoteMediator<Int, SearchVideoEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SearchVideoEntity>
    ): MediatorResult = withContext(scope.coroutineContext + Dispatchers.IO) {
        try {
            if (loadType == LoadType.PREPEND) return@withContext MediatorResult.Success(true)

            if (loadType == LoadType.REFRESH) {
                query = query.copy(pageToken = null)
            }

            val response = getItems(query, loadType)
            val data = (response as? ApiResponse.Success)?.data

            data?.let {
                query = query.copy(pageToken = data.nextPageToken)

                MediatorResult.Success(data.nextPageToken == null)
            } ?: MediatorResult.Error(Exception())

        } catch (e: IOException) {
            MediatorResult.Error(e)
        }
    }
}