package com.kouta.home.channel.vo

import androidx.paging.PagingData
import com.kouta.data.vo.entity.SubscriptionVideo
import kotlinx.coroutines.flow.Flow

sealed class ItemState {
    data object None : ItemState()

    data class Fetched(val itemFlow: Flow<PagingData<SubscriptionVideo>>) : ItemState()

    sealed class ZeroMatch : ItemState() {
        data object Subscription : ZeroMatch()
        data object SubscriptionVideo : ZeroMatch()
    }
}