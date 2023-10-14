package com.kouta.design.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kouta.design.resource.Red
import com.kouta.design.resource.White
import com.kouta.design.resource.YoutubeAnalyzeTheme

@Composable
fun LiveLabel(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Red)
            .then(modifier)
            .padding(horizontal = 12.dp)
    ) {
       Text(
           modifier = Modifier.align(Alignment.Center),
           text = "Live Now",
           style = typography.titleSmall.copy(White)
       )
    }
}

@Preview
@Composable
fun PreviewLiveLabel() {
    YoutubeAnalyzeTheme {
        LiveLabel()
    }
}