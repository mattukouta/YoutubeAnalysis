package com.kouta.home.top

import androidx.paging.PagingData
import com.kouta.auth.vo.LoginState
import com.kouta.auth.vo.User
import com.kouta.data.vo.ApiResponse
import com.kouta.data.vo.activities.ChannelActivities
import com.kouta.data.vo.entity.SubscriptionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class UiState(
    val loginState: LoginState = LoginState.None,
    val channel: ApiResponse<ChannelActivities.Response> = ApiResponse.Error.ParseException,
    val subscriptions: Flow<PagingData<SubscriptionEntity>> = emptyFlow()
) {
    sealed class LoginState {
        data object None :  LoginState()
        data object NotLogin : LoginState()
        data object Loading : LoginState()
        data class Fetched(val user: User) : LoginState()
        data class Error(val message: String) : LoginState()
    }
}
