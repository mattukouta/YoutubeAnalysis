package com.kouta.design.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kouta.design.resource.YoutubeAnalyzeTheme

@Composable
fun ErrorPanel(
    message: String
) {
    Text(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        text = message
    )
}

@Preview
@Composable
fun PreviewErrorPanel() {
    YoutubeAnalyzeTheme {
        ErrorPanel(message = "エラーが発生しました。")
    }
}