package com.kouta.home.top

import androidx.paging.PagingData
import com.kouta.auth.vo.LoginState
import com.kouta.data.vo.entity.SubscriptionEntity
import com.kouta.design.ScreenState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StateCreator @Inject constructor() {
    fun create(
        loginState: LoginState,
        isLoading: Boolean,
        subscriptionFlow: Flow<PagingData<SubscriptionEntity>>
    ) = UiState(
        screenState = when {
            isLoading -> ScreenState.Loading.Initial
            loginState is LoginState.Login -> ScreenState.Fetched.Success(loginState.user)
            loginState is LoginState.NotLogin -> ScreenState.NotLogin
            loginState is LoginState.Error -> ScreenState.Error("ログインで問題が発生しました。再度お試しください。")
            else -> ScreenState.None
        },
        subscriptions = subscriptionFlow
    )
}