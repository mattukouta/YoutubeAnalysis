package com.kouta.home.channel

import androidx.paging.PagingData
import com.kouta.data.enums.LiveBroadcastContent
import com.kouta.data.vo.entity.SubscriptionVideo
import com.kouta.extension.ScreenState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class UiState(
    val itemFlow: Flow<PagingData<SubscriptionVideo>> = emptyFlow(),
    val filter: LiveBroadcastContent = LiveBroadcastContent.UNKNOWN
)