package com.kouta.home.top

import androidx.paging.PagingData
import com.kouta.auth.vo.LoginState
import com.kouta.data.vo.entity.SubscriptionEntity
import com.kouta.extension.ScreenState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StateCreator @Inject constructor() {
    fun create(
        loginState: LoginState,
        isLoading: Boolean,
        subscriptionFlow: Flow<PagingData<SubscriptionEntity>>
    ) = UiState(
        loginState = when {
            isLoading -> UiState.LoginState.Loading
            loginState is LoginState.Login -> UiState.LoginState.Fetched(loginState.user)
            loginState is LoginState.NotLogin -> UiState.LoginState.NotLogin
            loginState is LoginState.Error -> UiState.LoginState.Error("ログインで問題が発生しました。再度お試しください。")
            else -> UiState.LoginState.None
        },
        subscriptions = subscriptionFlow
    )
}