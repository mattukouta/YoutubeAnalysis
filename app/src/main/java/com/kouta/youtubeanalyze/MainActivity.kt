package com.kouta.youtubeanalyze

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.kouta.home.homeScreen
import com.kouta.youtubeanalyze.ui.theme.YoutubeAnalyzeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            YoutubeAnalyzeTheme(
                dynamicColor = false
            ) {
                Surface {
                    NavHost(
                        navController = rememberNavController(),
                        startDestination = homeScreen
                    ) {
                        homeScreen()
                    }
                }
            }
        }
    }
}