package com.kouta.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kouta.design.NetworkImage
import com.kouta.home.HomeViewModel.Action
import kotlinx.coroutines.flow.map
import timber.log.Timber

const val homeScreen = "HomeScreen"
fun NavGraphBuilder.homeScreen() {
    composable(homeScreen) {
        HomeScreenRoute()
    }
}

@Composable
fun HomeScreenRoute(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dispatch = viewModel.dispatch

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            result.data?.let {
                dispatch(Action.LoginSuccess(it))
            } ?: dispatch(Action.LoginFailed)
        })

    LaunchedEffect(Unit) {
        viewModel.viewEvent.collect {
            when (it) {
                is HomeViewModel.ViewEvent.DebugLog -> {
                    Timber.d("ktakamat HomeViewModel ${it.message}")
                }
            }
        }
    }

    HomeScreen(
        uiState = uiState,
        onClickLogin = {
            dispatch(Action.Login(launcher = launcher))
        },
        onClickRefresh = {
            dispatch(Action.Refresh)
        },
        onClickLogout = {
            dispatch(Action.Logout)
        }
    )
}

@Composable
fun HomeScreen(
    uiState: UiState,
    onClickLogin: () -> Unit,
    onClickRefresh: () -> Unit,
    onClickLogout: () -> Unit
) {
    Scaffold {
        Column(modifier = Modifier.padding(it)) {
            if (!uiState.isLogin) {
                Button(onClick = onClickLogin) {
                    Text(text = "ログイン")
                }
            } else {
                Button(onClick = onClickRefresh) {
                    Text(text = "リフレッシュ")
                }
                Button(onClick = onClickLogout) {
                    Text(text = "ログアウト")
                }
                Text(
                    text = "ログイン済み"
                )
                uiState.user?.let {user ->
                    Text(
                        text = "name : ${user.name}"
                    )

                    NetworkImage(imageUrl = user.profileUrl)
                }
            }
        }
    }
}