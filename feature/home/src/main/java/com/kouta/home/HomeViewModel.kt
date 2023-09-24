package com.kouta.home

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kouta.auth.AuthRepository
import com.kouta.extension.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val stateCreator: StateCreator,
    private val authRepository: AuthRepository
) : ViewModel() {
    sealed class Action {
        data class Login(val launcher: ActivityResultLauncher<Intent>) : Action()
        data object Refresh : Action()
        data object Logout : Action()
        data class LoginSuccess(val intent: Intent) : Action()
        data object LoginFailed : Action()
    }

    sealed class ViewEvent {
        data class DebugLog(val message: String) : ViewEvent()
    }

    private val isLogin = authRepository.isLogin
    private val user = authRepository.user

    val uiState: StateFlow<UiState> = isLogin.combine(
        user
    ) { isLogin, user ->
        stateCreator.create(isLogin, user)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState())

    private val _viewEvent: Channel<ViewEvent> = Channel()
    val viewEvent = _viewEvent.receiveAsFlow()

    val dispatch: (Action) -> Unit = {
        when (it) {
            is Action.Login -> {
                login(it.launcher)
            }
            Action.Refresh -> {
                refresh()
            }
            Action.Logout -> {
                logout()
            }
            is Action.LoginSuccess -> {
                // TODO ログイン成功時の処理
                sendDebugLog("LoginSuccess")

                launch {
                    authRepository.requestAccessTokenFromIntent(it.intent)
//                    authRepository.getAccessToken()
                }
            }

            Action.LoginFailed -> {
                // TODO ログイン失敗時の処理
                sendDebugLog("LoginFailed")
            }
        }
    }

    private fun login(launcher: ActivityResultLauncher<Intent>) {

        authRepository.login(launcher)
    }
    private fun refresh() {
        launch {
            authRepository.requestAccessToken()
        }
    }

    private fun logout() {
        authRepository.logout()
    }

    private fun sendDebugLog(message: String) = launch {
        _viewEvent.send(ViewEvent.DebugLog(message))
    }
}

