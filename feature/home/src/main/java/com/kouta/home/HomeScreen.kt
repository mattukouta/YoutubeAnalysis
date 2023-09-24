package com.kouta.home

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kouta.design.compose.dialog.LoadingPanel
import com.kouta.design.compose.NetworkImage
import com.kouta.design.compose.UnknownProfileImage
import com.kouta.design.resource.YoutubeAnalyzeTheme
import com.kouta.home.HomeViewModel.Action
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
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    result.data?.let {
                        dispatch(Action.LoginSuccess(it))
                    } ?: dispatch(Action.LoginFailed)
                }
                Activity.RESULT_CANCELED -> {
                    dispatch(Action.LoginCanceled)
                }
                else -> {
                    dispatch(Action.LoginFailed)
                }
            }
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
        onClickLogout = {
            dispatch(Action.Logout)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: UiState,
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "home")
                },
                actions = {
                    val user = uiState.user
                    if (uiState.isLogin && user != null) {
                        Box {
                            var expanded by remember { mutableStateOf(false) }

                            NetworkImage(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant)
                                    .clickable(
                                        indication = rememberRipple(),
                                        interactionSource = remember { MutableInteractionSource() },
                                        onClick = {
                                            expanded = true
                                        }
                                    ),
                                imageUrl = user.profileUrl
                            )

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(text = "ログアウト") },
                                    onClick = onClickLogout
                                )
                            }
                        }
                    } else {
                        UnknownProfileImage(size = 24.dp, onClick = onClickLogin)
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                }
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            Column {
                if (uiState.isLogin) {
                    Text(
                        text = "ログイン済み"
                    )
                } else {
                    NotLogin(onClickLogin)
                }
            }

            if (uiState.isShowLoading) {
                LoadingPanel()
            }
        }
    }
}

@Composable
fun NotLogin(onClickLogin: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "ログインできていません。データ取得解析を利用する場合ログインしてください。")
        Button(onClick = onClickLogin) {
            Text(modifier = Modifier, text = "ログインはこちら")
        }
    }
}

@Preview
@Composable
fun PreviewHomeScreen() {
    YoutubeAnalyzeTheme {
        HomeScreen(uiState = UiState(isLogin = true), onClickLogin = {}, onClickLogout = {})
    }
}