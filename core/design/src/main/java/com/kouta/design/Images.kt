package com.kouta.design

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@Composable
fun NetworkImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    isCrossFade: Boolean = true,
    @DrawableRes painterRes: Int? = null,
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
            placeholder = painterRes?.let { painterResource(it) },
            error = errorRes?.let { painterResource(it) }
        ),
        contentDescription = contentDescription
    )
}