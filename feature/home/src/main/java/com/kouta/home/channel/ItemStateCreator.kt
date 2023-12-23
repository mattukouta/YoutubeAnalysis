package com.kouta.home.channel

import androidx.paging.PagingData
import com.kouta.data.vo.entity.SubscriptionVideo
import com.kouta.home.channel.vo.ItemState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ItemStateCreator @Inject constructor() {
//    fun create(
//        subscriptionResultsAvailable: Int?,
//        subscriptionVideoResultsAvailable: Int?
//    ) = when {
//        (subscriptionResultsAvailable ?: 0) > 0 && (subscriptionVideoResultsAvailable ?: 0) > 0 -> ItemState.Fetched(itemFlow)
//        subscriptionResultsAvailable == 0 -> ItemState.ZeroMatch.Subscription
//        subscriptionVideoResultsAvailable == 0 -> ItemState.ZeroMatch.SubscriptionVideo
//        else -> ItemState.None
//    }
}