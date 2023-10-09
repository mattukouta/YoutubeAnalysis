package com.kouta.design.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kouta.design.resource.YoutubeAnalyzeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YoutubeTopAppBar(
    title: String,
    onClickBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            onClickBack?.let {
                IconButton(
                    onClick = it
                ) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
                }
            }
        },
        actions = actions
    )
}

@Preview
@Composable
fun PreviewYoutubeTopAppBar() {
    YoutubeAnalyzeTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            YoutubeTopAppBar(title = "home")
            YoutubeTopAppBar(title = "home", onClickBack = {})
        }
    }
}