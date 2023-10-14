package com.kouta.youtubeanalyze

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.kouta.home.top.homeScreen
import com.kouta.design.resource.YoutubeAnalyzeTheme
import com.kouta.home.top.HomeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val navController = rememberNavController()
            YoutubeAnalyzeTheme(
                dynamicColor = false
            ) {
                Surface {
                    NavHost(
                        navController = navController,
                        startDestination = HomeScreen.HOME_TOP.name
                    ) {
                        homeScreen(
                            onNavigateFavoriteChannelContents = {
                                navController.navigate(HomeScreen.HOME_FAVORITE_CHANNEL_CONTENTS.name)
                            },
                            onClickBack = {
                                navController.safePopBackStack()
                            }
                        )
                    }
                }
            }
        }
    }

    private fun NavController.safePopBackStack() {
        if (previousBackStackEntry != null) {
            popBackStack()
        }
    }
}