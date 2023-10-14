package com.kouta.home.top

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.kouta.auth.AuthRepository
import com.kouta.auth.vo.LoginState
import com.kouta.data.usecase.channels.GetChannelsUseCase
import com.kouta.data.usecase.subscriptions.GetSubscriptionsUseCase
import com.kouta.data.vo.channels.Channels
import com.kouta.data.vo.entity.SubscriptionEntity
import com.kouta.extension.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val stateCreator: StateCreator,
    private val authRepository: AuthRepository,
    private val getChannelsUseCase: GetChannelsUseCase,
    private val getSubscriptionsUseCase: GetSubscriptionsUseCase
) : ViewModel() {
    sealed class Action {
        data class Login(val launcher: ActivityResultLauncher<Intent>) : Action()
        data object Logout : Action()
        data class LoginSuccess(val intent: Intent) : Action()
        data object LoginCanceled : Action()
        data object LoginFailed : Action()
        data object Request : Action()
        data class OnClickChannel(val channelId: String) : Action()
        data object OnClickFavoriteChannelContents : Action()
    }

    sealed class ViewEvent {
        data class DebugLog(val message: String) : ViewEvent()
        data class NavigateYoutubeChannel(val channelId: String) : ViewEvent()
        data object NavigateFavoriteChannelContents : ViewEvent()
    }

    private val isLoading = MutableStateFlow(false)
    private val loginState = authRepository.loginState
    private val subscriptionFlow: MutableStateFlow<Flow<PagingData<SubscriptionEntity>>> =
        MutableStateFlow(
            emptyFlow()
        )

    val uiState: StateFlow<UiState> = combine(
        loginState,
        isLoading,
        subscriptionFlow
    ) { loginState, isLoading, subscriptionFlow ->
        stateCreator.create(loginState, isLoading, subscriptionFlow)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState())

    private val _viewEvent: Channel<ViewEvent> = Channel()
    val viewEvent = _viewEvent.receiveAsFlow()

    val dispatch: (Action) -> Unit = {
        launch {
            when (it) {
                is Action.Login -> login(it.launcher)

                Action.Logout -> logout()

                is Action.LoginSuccess -> {

                    // TODO ログイン成功時の処理
                    sendDebugLog("LoginSuccess")

                    authRepository.requestAccessTokenFromIntent(
                        it.intent,
                        onSuccess = { finishLoading() })
                }

                Action.LoginCanceled -> finishLoading()

                Action.LoginFailed -> {
                    // TODO ログイン失敗時の処理
                    sendDebugLog("LoginFailed")
                }

                Action.Request -> {
                    launch {
//                    getChannelActivities(true)
//                    getChannels()

//                    getSubscriptions()
                    }
                }

                is Action.OnClickChannel -> navigateYoutubeChannel(it.channelId)

                Action.OnClickFavoriteChannelContents -> navigateFavoriteChannelContents()
            }
        }
    }

    init {
        launch {
            loginState.collect {
                if (it is LoginState.Login) {
//                    subscriptionFlow.emit(getSubscriptions())
                    subscriptionFlow.emit(
                        flowOf(
                            PagingData.from(
                                listOf(
                                    SubscriptionEntity(
                                        id = "1",
                                        title = "title1",
                                        imageUrl = "https://placehold.jp/150x150.png?text=placeholder"
                                    ),
                                    SubscriptionEntity(
                                        id = "2",
                                        title = "title2",
                                        imageUrl = "https://placehold.jp/150x150.png?text=placeholder"
                                    ),
                                    SubscriptionEntity(
                                        id = "3",
                                        title = "title3",
                                        imageUrl = "https://placehold.jp/150x150.png?text=placeholder"
                                    ),
                                    SubscriptionEntity(
                                        id = "4",
                                        title = "title4",
                                        imageUrl = "https://placehold.jp/150x150.png?text=placeholder"
                                    ),
                                    SubscriptionEntity(
                                        id = "5",
                                        title = "title5",
                                        imageUrl = "https://placehold.jp/150x150.png?text=placeholder"
                                    ),
                                    SubscriptionEntity(
                                        id = "6",
                                        title = "title6",
                                        imageUrl = "https://placehold.jp/150x150.png?text=placeholder"
                                    ),
                                    SubscriptionEntity(
                                        id = "7",
                                        title = "title7",
                                        imageUrl = "https://placehold.jp/150x150.png?text=placeholder"
                                    ),
                                    SubscriptionEntity(
                                        id = "8",
                                        title = "title8",
                                        imageUrl = "https://placehold.jp/150x150.png?text=placeholder"
                                    ),
                                    SubscriptionEntity(
                                        id = "9",
                                        title = "title9",
                                        imageUrl = "https://placehold.jp/150x150.png?text=placeholder"
                                    ),
                                    SubscriptionEntity(
                                        id = "10",
                                        title = "title10",
                                        imageUrl = "https://placehold.jp/150x150.png?text=placeholder"
                                    ),
                                    SubscriptionEntity(
                                        id = "11",
                                        title = "title11",
                                        imageUrl = "https://placehold.jp/150x150.png?text=placeholder"
                                    ),
                                    SubscriptionEntity(
                                        id = "12",
                                        title = "title12",
                                        imageUrl = "https://placehold.jp/150x150.png?text=placeholder"
                                    ),
                                    SubscriptionEntity(
                                        id = "13",
                                        title = "title13",
                                        imageUrl = "https://placehold.jp/150x150.png?text=placeholder"
                                    ),
                                    SubscriptionEntity(
                                        id = "14",
                                        title = "title14",
                                        imageUrl = "https://placehold.jp/150x150.png?text=placeholder"
                                    ),
                                    SubscriptionEntity(
                                        id = "15",
                                        title = "title15",
                                        imageUrl = "https://placehold.jp/150x150.png?text=placeholder"
                                    ),
                                )
                            )
                        )
                    )
                }
            }
        }
    }

    private suspend fun getChannels() {
        getChannelsUseCase.get(
            Channels.RequestQuery(
                part = listOf(Channels.RequestQuery.Part.ID),
                filter = Channels.RequestQuery.Filter.Mine(true),
                maxResults = 1
            )
        )
    }

    private fun getSubscriptions() = getSubscriptionsUseCase.get(scope = viewModelScope)

    private fun login(launcher: ActivityResultLauncher<Intent>) {
        startLoading()

        authRepository.login(launcher)
    }

    private fun refresh() {
        launch {
            authRepository.requestAccessToken()
        }
    }

    private fun logout() {
        startLoading()

        authRepository.logout(onSuccess = { finishLoading() })
    }

    private fun sendDebugLog(message: String) = launch {
        _viewEvent.send(ViewEvent.DebugLog(message))
    }

    private fun startLoading() {
        isLoading.value = true
    }

    private fun finishLoading() {
        isLoading.value = false
    }

    private suspend fun navigateYoutubeChannel(channelId: String) {
        _viewEvent.send(ViewEvent.NavigateYoutubeChannel(channelId))
    }

    private suspend fun navigateFavoriteChannelContents() {
        _viewEvent.send(ViewEvent.NavigateFavoriteChannelContents)
    }
}

