package com.kouta.data.usecase.subscriptions

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.cachedIn
import com.kouta.data.repository.SubscriptionsRepository
import com.kouta.data.vo.ApiResponse
import com.kouta.data.vo.entity.SubscriptionEntity
import com.kouta.data.vo.subscriptions.Subscriptions.RequestQuery
import com.kouta.data.vo.subscriptions.Subscriptions.RequestQuery.Filter
import com.kouta.data.vo.subscriptions.Subscriptions.RequestQuery.Order
import com.kouta.data.vo.subscriptions.Subscriptions.RequestQuery.Part
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import timber.log.Timber
import javax.inject.Inject

class GetSubscriptionsUseCase @Inject constructor(
    private val subscriptionsRepository: SubscriptionsRepository
) {
    companion object {
        const val DATABASE_SIZE = 5
    }

    @OptIn(ExperimentalPagingApi::class)
    fun get(
        scope: CoroutineScope
    ) = Pager(
        config = PagingConfig(pageSize = DATABASE_SIZE, initialLoadSize = DATABASE_SIZE),
        pagingSourceFactory = { subscriptionsRepository.get() },
        remoteMediator = SubscriptionMediator(
            scope = scope,
            query = RequestQuery(
                part = listOf(
                    Part.SNIPPET
                ),
                filter = Filter.Mine,
                forChannelId = listOf(),
                maxResults = DATABASE_SIZE,
                order = Order.RELEVANCE,
                pageToken = null
            ),
            repository = subscriptionsRepository
        )
    ).flow.cachedIn(scope)
}

@OptIn(ExperimentalPagingApi::class)
class SubscriptionMediator(
    private val scope: CoroutineScope,
    private var query: RequestQuery,
    private val repository: SubscriptionsRepository
) : RemoteMediator<Int, SubscriptionEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SubscriptionEntity>
    ): MediatorResult = withContext(scope.coroutineContext + Dispatchers.IO) {
        try {
            if (loadType == LoadType.PREPEND) return@withContext MediatorResult.Success(true)

            if (loadType == LoadType.REFRESH) {
                repository.deleteAll()
                query = query.copy(pageToken = null)
//                query.update { it.copy(pageToken = null) }
            }

            if (query.pageToken == null) {
                // TODO 初回読み込み時の処理
            }

            val response = repository.getSubscriptions(query.toQueryMap())
            val data = (response as? ApiResponse.Success)?.data

            Timber.d("ktakamat usecase response=$response")

            data?.let {
                query = query.copy(pageToken = data.nextPageToken)

                MediatorResult.Success(data.nextPageToken == null)
            } ?: MediatorResult.Error(Exception())

        } catch (e: IOException) {
            MediatorResult.Error(e)
        }
    }

    private inline fun <RequestQuery> RequestQuery.update(function: (RequestQuery) -> RequestQuery) {
        function(this)
    }
}