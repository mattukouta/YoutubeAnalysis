package com.kouta.design.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kouta.design.resource.YoutubeAnalyzeTheme

@Composable
fun NotLoginPanel(mainText : String, onClickLogin: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = mainText)
        Button(onClick = onClickLogin) {
            Text(modifier = Modifier, text = "ログインはこちら")
        }
    }
}

@Preview
@Composable
fun PreviewNotLoginPanel() {
    YoutubeAnalyzeTheme {
        NotLoginPanel(mainText = "ログインできていません。データ取得解析を利用する場合ログインしてください。", onClickLogin = {})
    }
}