package com.kouta.home.channel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kouta.data.enums.LiveBroadcastContent
import com.kouta.data.vo.Thumbnail
import com.kouta.data.vo.entity.SubscriptionEntity
import com.kouta.data.vo.entity.SubscriptionVideo
import com.kouta.data.vo.entity.VideoEntity
import com.kouta.data.vo.video.Video
import com.kouta.design.R
import com.kouta.extension.ScreenState
import com.kouta.design.compose.ErrorPanel
import com.kouta.design.compose.LiveLabel
import com.kouta.design.compose.NetworkImage
import com.kouta.design.compose.NetworkImageCircle
import com.kouta.design.compose.YoutubeTopAppBar
import com.kouta.design.compose.dialog.LoadingPanel
import com.kouta.design.resource.White
import com.kouta.design.resource.YoutubeAnalyzeTheme
import com.kouta.extension.toPagingScreenState
import kotlinx.coroutines.flow.Flow

@Composable
fun FavoriteChannelContentsRoute(
    viewModel: FavoriteChannelContentsViewModel = hiltViewModel(),
    onClickBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val dispatch = viewModel.dispatch

    LaunchedEffect(Unit) {
        viewModel.viewEvent.collect {
            when (it) {
                FavoriteChannelContentsViewModel.ViewEvent.PopBackStack -> onClickBack()
            }
        }
    }

    FavoriteChannelContentsScreen(
        uiState,
        onClickBack = onClickBack,
        onClickFilter = { dispatch(FavoriteChannelContentsViewModel.Action.OnClickFilter(it)) }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteChannelContentsScreen(
    uiState: UiState,
    onClickBack: () -> Unit,
    onClickFilter: (filter: LiveBroadcastContent) -> Unit
) {
    Scaffold(
        topBar = {
            YoutubeTopAppBar(title = "登録チャンネルコンテンツ一覧", onClickBack = onClickBack)
        }
    ) { paddingValues ->
        val isLiveSelected = uiState.filter == LiveBroadcastContent.LIVE
        val screenState: ScreenState<LazyPagingItems<SubscriptionVideo>> = uiState.itemFlow.collectAsLazyPagingItems().toPagingScreenState()

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when (screenState) {
                is ScreenState.Fetched.Success -> {
                    val items = screenState.data

                    Column(
                        modifier = Modifier.padding(paddingValues = paddingValues)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            FilterChip(
                                isLiveSelected = uiState.filter == LiveBroadcastContent.LIVE,
                                onClickFilter = { onClickFilter(LiveBroadcastContent.LIVE) },
                                label = "配信中"
                            )

                            FilterChip(
                                isLiveSelected = uiState.filter == LiveBroadcastContent.UPCOMING,
                                onClickFilter = { onClickFilter(LiveBroadcastContent.UPCOMING) },
                                label = "配信予定"
                            )
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            items(items.itemCount) {
                                items[it]?.let { item ->
                                    ContentItemAt(
                                        item = item
                                    )
                                }
                            }
                        }
                    }
                }

                is ScreenState.Fetched.ZeroMatch -> {
                    Text(modifier = Modifier.align(Alignment.Center), text = "ゼロマッチ")
                }

                ScreenState.Loading.Initial -> LoadingPanel()
                is ScreenState.Error -> ErrorPanel(message = "error")
                ScreenState.None -> {}
            }
        }
    }
}

@Composable
fun ContentItemAt(
    item: SubscriptionVideo
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = rememberRipple(),
                interactionSource = remember { MutableInteractionSource() },
                onClick = {}
            )
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        val (thumbnail, liveLabel, profileImage, title, channelTitle) = createRefs()

        val highThumbnail = item.video.snippet?.thumbnails?.getHighThumbnail()

        highThumbnail?.let {
            val thumbnailWidth = it.width ?: return@let
            val thumbnailHeight = it.height ?: return@let

            val aspectRatio = thumbnailWidth.toFloat() / thumbnailHeight.toFloat()

            NetworkImage(
                modifier = Modifier
                    .constrainAs(thumbnail) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
                    .aspectRatio(aspectRatio),
                imageUrl = it.url,
                placeHolderRes = R.drawable.ic_person
            )

            if (item.video.snippet?.liveBroadcastContent == LiveBroadcastContent.LIVE) {
                LiveLabel(
                    modifier = Modifier.constrainAs(liveLabel) {
                        end.linkTo(thumbnail.end, 4.dp)
                        bottom.linkTo(thumbnail.bottom, 4.dp)
                    }
                )
            }

            NetworkImageCircle(
                modifier = Modifier
                    .constrainAs(profileImage) {
                        top.linkTo(thumbnail.bottom, 8.dp)
                        start.linkTo(thumbnail.start, 4.dp)
                    }
                    .size(36.dp),
                imageUrl = item.subscription.imageUrl,
                placeHolderRes = R.drawable.ic_person
            )

            Text(
                modifier = Modifier.constrainAs(title) {
                    top.linkTo(profileImage.top)
                    linkTo(profileImage.end, thumbnail.end, 16.dp)
                    width = Dimension.fillToConstraints
                },
                maxLines = 2,
                overflow = Ellipsis,
                text = item.video.snippet?.title ?: "",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                modifier = Modifier.constrainAs(channelTitle) {
                    top.linkTo(title.bottom)
                    linkTo(title.start, title.end, 4.dp)
                    width = Dimension.fillToConstraints
                },
                text = item.subscription.title,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Preview
@Composable
fun PreviewSearchItemAt() {
    YoutubeAnalyzeTheme {
        ContentItemAt(
            item = SubscriptionVideo(
                subscription = SubscriptionEntity(
                    id = "sample channelId",
                    title = "sample title",
                    imageUrl = "sample imageUrl"

                ),
                video = VideoEntity(
                    videoId = "",
                    index = 0,
                    snippet = VideoEntity.Snippet(
                        publishedAt = "",
                        channelId = "sample channelId",
                        title = "sample title",
                        description = "sample description",
                        thumbnails = Video.Response.Video.Snippet.Thumbnails(
                            default = Thumbnail(url = "", width = null, height = null),
                            medium = Thumbnail(url = "", width = null, height = null),
                            high = Thumbnail(url = "", width = null, height = null),
                            standard = Thumbnail(url = "", width = null, height = null),
                            maxres = Thumbnail(url = "", width = null, height = null)
                        ),
                        channelTitle = "sample channelTitle",
                        liveBroadcastContent = LiveBroadcastContent.LIVE
                    ),
                    liveStreamingDetails = null
                ),
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChip(
    isLiveSelected: Boolean,
    onClickFilter: () -> Unit,
    label: String,
) {
    FilterChip(
        selected = isLiveSelected,
        onClick = onClickFilter,
        label = {
            Text(
                modifier = Modifier.padding(horizontal = if (isLiveSelected) 0.dp else 12.dp),
                text = label
            )
        },
        leadingIcon = if (isLiveSelected) {
            {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check),
                    contentDescription = null,
                    tint = White
                )
            }
        } else {
            null
        }
    )
}

@Preview
@Composable
fun PreviewFavoriteChannelContentsScreen() {
    YoutubeAnalyzeTheme {
        FavoriteChannelContentsRoute(
            onClickBack = {}
        )
    }
}