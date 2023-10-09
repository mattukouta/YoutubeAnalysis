package com.kouta.design.compose

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.kouta.design.R
import com.kouta.design.resource.YoutubeAnalyzeTheme

@Composable
fun NetworkImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    isCrossFade: Boolean = true,
    @DrawableRes placeHolderRes: Int? = null,
    @DrawableRes errorRes: Int? = null,
    contentDescription: String? = null
) {
    Image(
        modifier = modifier,
        painter = rememberAsyncImagePainter(
            model = ImageRequest
                .Builder(LocalContext.current)
                .data(data = imageUrl)
                .crossfade(isCrossFade)
                .build(),
            placeholder = placeHolderRes?.let { painterResource(it) },
            error = errorRes?.let { painterResource(it) }
        ),
        contentDescription = contentDescription
    )
}

@Composable
fun NetworkImageCircle(
    modifier: Modifier = Modifier,
    imageUrl: String,
    isCrossFade: Boolean = true,
    @DrawableRes placeHolderRes: Int? = null,
    @DrawableRes errorRes: Int? = null,
    backGroundColor: Color = colorScheme.onSurfaceVariant,
    contentDescription: String? = null
) {
    NetworkImage(
        modifier = Modifier
            .clip(CircleShape)
            .background(backGroundColor)
            .then(modifier),
        imageUrl = imageUrl,
        isCrossFade = isCrossFade,
        placeHolderRes = placeHolderRes,
        errorRes = errorRes,
        contentDescription = contentDescription
    )
}

@Composable
fun UnknownProfileImage(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    onClick: () -> Unit
) {
    IconButton(
        modifier = Modifier
            .size(size)
            .then(modifier), onClick = onClick
    ) {
        Icon(
            modifier = Modifier
                .size(size)
                .background(colorScheme.surfaceVariant),
            painter = painterResource(id = R.drawable.ic_person),
            contentDescription = "未ログイン",
            tint = colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
fun PreviewNetworkImage() {
    YoutubeAnalyzeTheme {
        NetworkImage(imageUrl = "", placeHolderRes = R.drawable.ic_person)
    }
}


@Preview
@Composable
fun PreviewNetworkImageCircle() {
    YoutubeAnalyzeTheme {
        NetworkImageCircle(imageUrl = "", placeHolderRes = R.drawable.ic_person)
    }
}

@Preview
@Composable
fun PreviewUnknownProfileImage() {
    YoutubeAnalyzeTheme(
        darkTheme = true
    ) {
        UnknownProfileImage(
            onClick = {}
        )
    }
}