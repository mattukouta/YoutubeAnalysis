package com.kouta.data.usecase.subscriptions

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.cachedIn
import com.kouta.data.repository.SubscriptionRepository
import com.kouta.data.vo.ApiResponse
import com.kouta.data.vo.entity.SubscriptionEntity
import com.kouta.data.vo.subscriptions.Subscriptions
import com.kouta.data.vo.subscriptions.Subscriptions.RequestQuery
import com.kouta.data.vo.subscriptions.Subscriptions.RequestQuery.Filter
import com.kouta.data.vo.subscriptions.Subscriptions.RequestQuery.Order
import com.kouta.data.vo.subscriptions.Subscriptions.RequestQuery.Part
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import javax.inject.Inject

class GetSubscriptionsUseCase @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
) {
    companion object {
        const val DATABASE_SIZE = 5
    }

    @OptIn(ExperimentalPagingApi::class)
    fun get(
        scope: CoroutineScope
    ) = Pager(
        config = PagingConfig(pageSize = DATABASE_SIZE, initialLoadSize = DATABASE_SIZE),
        pagingSourceFactory = { subscriptionRepository.get() },
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
            getItems = { query, loadType ->
                subscriptionRepository.getSubscriptions(query, loadType)
            }
        )
    ).flow.cachedIn(scope)
}

@OptIn(ExperimentalPagingApi::class)
class SubscriptionMediator(
    private val scope: CoroutineScope,
    private var query: RequestQuery,
    private val getItems: suspend (query: RequestQuery, loadType: LoadType) -> ApiResponse<Subscriptions.Response>
) : RemoteMediator<Int, SubscriptionEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SubscriptionEntity>
    ): MediatorResult = withContext(scope.coroutineContext + Dispatchers.IO) {
        try {
            if (loadType == LoadType.PREPEND) return@withContext MediatorResult.Success(true)

            if (loadType == LoadType.REFRESH) {
                query = query.copy(pageToken = null)
            }

            when (val response = getItems(query, loadType)) {
                is ApiResponse.Success -> {
                    query = query.copy(pageToken = response.data.nextPageToken)

                    MediatorResult.Success(response.data.prevPageToken == null)
                }
                is ApiResponse.Error -> MediatorResult.Error(Exception())
            }

        } catch (e: IOException) {
            MediatorResult.Error(e)
        }
    }
}