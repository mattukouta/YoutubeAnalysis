package com.kouta.design.compose.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.kouta.design.resource.YoutubeAnalyzeTheme

@Composable
fun LoadingPanel() {
    Dialog(onDismissRequest = { }) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview
@Composable
fun PreviewLoadingPanel() {
    YoutubeAnalyzeTheme() {
        LoadingPanel()
    }
}