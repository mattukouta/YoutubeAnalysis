package com.kouta.home.top

import androidx.paging.PagingData
import com.kouta.auth.vo.User
import com.kouta.data.vo.ApiResponse
import com.kouta.data.vo.activities.ChannelActivities
import com.kouta.data.vo.entity.SubscriptionEntity
import com.kouta.design.ScreenState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class UiState(
    val screenState: ScreenState<User> = ScreenState.None,
    val channel: ApiResponse<ChannelActivities.Response> = ApiResponse.Error.ParseException,
    val subscriptions: Flow<PagingData<SubscriptionEntity>> = emptyFlow()
)
