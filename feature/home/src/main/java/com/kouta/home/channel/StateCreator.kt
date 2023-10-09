package com.kouta.home.channel

import androidx.paging.PagingData
import com.kouta.data.enums.LiveBroadcastContent
import com.kouta.data.vo.entity.SubscriptionVideo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StateCreator @Inject constructor() {
    fun create(
        itemFlow: Flow<PagingData<SubscriptionVideo>>,
        filters: LiveBroadcastContent
    ) = UiState(
        itemFlow = itemFlow,
        filter = filters
    )
}