package com.kouta.home.top

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.kouta.auth.vo.User
import com.kouta.data.vo.ApiResponse
import com.kouta.data.vo.entity.SubscriptionEntity
import com.kouta.design.R
import com.kouta.design.ScreenState
import com.kouta.design.compose.ErrorPanel
import com.kouta.design.compose.NetworkImage
import com.kouta.design.compose.NetworkImageCircle
import com.kouta.design.compose.NotLoginPanel
import com.kouta.design.compose.UnknownProfileImage
import com.kouta.design.compose.YoutubeTopAppBar
import com.kouta.design.compose.dialog.LoadingPanel
import com.kouta.design.resource.YoutubeAnalyzeTheme
import com.kouta.home.channel.FavoriteChannelContentsRoute
import com.kouta.home.top.HomeViewModel.Action
import com.kouta.home.top.HomeViewModel.ViewEvent
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber

enum class HomeScreen {
    HOME_TOP,
    HOME_FAVORITE_CHANNEL_CONTENTS;

}
fun NavGraphBuilder.homeScreen(
    onNavigateFavoriteChannelContents: () -> Unit,
    onClickBack: () -> Unit
) {
    composable(HomeScreen.HOME_TOP.name) {
        HomeScreenRoute(
            onNavigateFavoriteChannelContents = onNavigateFavoriteChannelContents
        )
    }
    composable(HomeScreen.HOME_FAVORITE_CHANNEL_CONTENTS.name) {
        FavoriteChannelContentsRoute(
            onClickBack = onClickBack
        )
    }
}

@Composable
fun HomeScreenRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateFavoriteChannelContents: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val dispatch = viewModel.dispatch
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            Timber.d("ktakamat onResult")
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
                is ViewEvent.DebugLog -> {
                    Timber.d("ktakamat HomeViewModel ${it.message}")
                }

                is ViewEvent.NavigateYoutubeChannel -> {
                    val url = "https://www.youtube.com/${it.channelId}"
                    try {
                        Intent(Intent.ACTION_VIEW).also {
                            it.setPackage("com.google.android.youtube")
                            it.data = Uri.parse(url)
                            context.startActivity(it)
                        }
                    } catch (e: ActivityNotFoundException) {
                        navigateCustomTab(url, context)
                    }
                }

                ViewEvent.NavigateFavoriteChannelContents -> onNavigateFavoriteChannelContents()
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
        },
        onClickRequest = {
            dispatch(Action.Request)
        },
        onClickChannel = {
            dispatch(Action.OnClickChannel(it))
        },
        onClickFavoriteChannelContentsList = {
            dispatch(Action.OnClickFavoriteChannelContents)
        }
    )
}

private fun navigateCustomTab(url: String, context: Context) {
    val uri = Uri.parse(url)
    CustomTabsIntent.Builder().also { builder ->
        builder.setShowTitle(true)
        builder.build().also {
            it.launchUrl(context, uri)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: UiState,
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit,
    onClickRequest: () -> Unit,
    onClickChannel: (channelId: String) -> Unit,
    onClickFavoriteChannelContentsList: () -> Unit
) {
    Scaffold(
        topBar = {
            YoutubeTopAppBar(
                title = "home",
                actions = {
                    val screenState = uiState.screenState
                    if (screenState is ScreenState.Fetched.Success) {
                        Box {
                            var expanded by remember { mutableStateOf(false) }

                            NetworkImageCircle(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable(
                                        indication = rememberRipple(),
                                        interactionSource = remember { MutableInteractionSource() },
                                        onClick = {
                                            expanded = true
                                        }
                                    ),

                                imageUrl = screenState.data?.profileUrl ?: ""
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
    ) { paddingValues ->
        val subscriptions = uiState.subscriptions.collectAsLazyPagingItems()
        val subscriptionListState = rememberLazyListState()

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (val screenState = uiState.screenState) {
                ScreenState.Loading.Initial -> {
                    LoadingPanel()
                }

                ScreenState.NotLogin -> {
                    NotLoginPanel(
                        mainText = "ログインできていません。データ取得解析を利用する場合ログインしてください。",
                        onClickLogin = onClickLogin
                    )
                }

                is ScreenState.Fetched.Success -> {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(text = "登録チャンネル一覧")

                        LazyRow(
                            modifier = Modifier.padding(top = 8.dp),
                            state = subscriptionListState,
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(subscriptions.itemCount) {
                                subscriptions[it]?.let { subscription ->
                                    SubscriptionAt(
                                        subscription = subscription,
                                        onClick = { onClickChannel(subscription.id) })
                                }
                            }
                        }

                        Button(
                            modifier = Modifier
                                .padding(top = 16.dp, start = 32.dp, end = 32.dp)
                                .fillMaxWidth(),
                            onClick = onClickFavoriteChannelContentsList
                        ) {
                            Text(text = "登録チャンネルのコンテンツ一覧画面へ")
                        }
                    }
                }

                is ScreenState.Error -> {
                    ErrorPanel(
                        screenState.message
                    )
                }

                is ScreenState.Loading.Refreshing<*> -> {}
                ScreenState.Fetched.ZeroMatch -> {}
                ScreenState.None -> {}
            }
        }
    }
}

@Composable
fun SubscriptionAt(subscription: SubscriptionEntity, onClick: () -> Unit) {
    NetworkImage(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .size(100.dp)
            .clickable(
                indication = rememberRipple(),
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        imageUrl = subscription.imageUrl,
        placeHolderRes = R.drawable.ic_person
    )
}

@Preview
@Composable
fun PreviewSubscriptionAt() {
    YoutubeAnalyzeTheme {
        SubscriptionAt(
            SubscriptionEntity(
                id = "lacus",
                title = "pericula",
                imageUrl = "https://search.yahoo.com/search?p=eum"
            ),
            onClick = {}
        )
    }
}

@Preview
@Composable
fun PreviewHomeScreen() {
    YoutubeAnalyzeTheme {
        HomeScreen(
            uiState = UiState(
                screenState = ScreenState.Fetched.Success(data = User.fixture()),
                channel = ApiResponse.Error.ParseException,
                subscriptions = flowOf(
                    PagingData.from(
                        listOf(
                            SubscriptionEntity(
                                id = "adolescens",
                                title = "eget",
                                imageUrl = "https://duckduckgo.com/?q=tamquam"
                            ),
                            SubscriptionEntity(
                                id = "adolescens",
                                title = "eget",
                                imageUrl = "https://duckduckgo.com/?q=tamquam"
                            ),
                            SubscriptionEntity(
                                id = "adolescens",
                                title = "eget",
                                imageUrl = "https://duckduckgo.com/?q=tamquam"
                            ),
                            SubscriptionEntity(
                                id = "adolescens",
                                title = "eget",
                                imageUrl = "https://duckduckgo.com/?q=tamquam"
                            ),
                            SubscriptionEntity(
                                id = "adolescens",
                                title = "eget",
                                imageUrl = "https://duckduckgo.com/?q=tamquam"
                            ),
                            SubscriptionEntity(
                                id = "adolescens",
                                title = "eget",
                                imageUrl = "https://duckduckgo.com/?q=tamquam"
                            )
                        )
                    )
                )
            ),
            onClickLogin = {},
            onClickLogout = {},
            onClickRequest = {},
            onClickChannel = {},
            onClickFavoriteChannelContentsList = {}
        )
    }
}