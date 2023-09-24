package com.kouta.youtubeanalyze.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.kouta.design.resource.BackgroundDark
import com.kouta.design.resource.BackgroundLight
import com.kouta.design.resource.ErrorContainerDark
import com.kouta.design.resource.ErrorContainerLight
import com.kouta.design.resource.ErrorDark
import com.kouta.design.resource.ErrorLight
import com.kouta.design.resource.InverseOnSurfaceDark
import com.kouta.design.resource.InverseOnSurfaceLight
import com.kouta.design.resource.InversePrimaryDark
import com.kouta.design.resource.InversePrimaryLight
import com.kouta.design.resource.InverseSurfaceDark
import com.kouta.design.resource.InverseSurfaceLight
import com.kouta.design.resource.OnBackgroundDark
import com.kouta.design.resource.OnBackgroundLight
import com.kouta.design.resource.OnErrorContainerDark
import com.kouta.design.resource.OnErrorContainerLight
import com.kouta.design.resource.OnErrorDark
import com.kouta.design.resource.OnErrorLight
import com.kouta.design.resource.OnPrimaryContainerDark
import com.kouta.design.resource.OnPrimaryContainerLight
import com.kouta.design.resource.OnPrimaryDark
import com.kouta.design.resource.OnPrimaryLight
import com.kouta.design.resource.OnSecondaryContainerDark
import com.kouta.design.resource.OnSecondaryContainerLight
import com.kouta.design.resource.OnSecondaryDark
import com.kouta.design.resource.OnSecondaryLight
import com.kouta.design.resource.OnSurfaceDark
import com.kouta.design.resource.OnSurfaceLight
import com.kouta.design.resource.OnSurfaceVariantDark
import com.kouta.design.resource.OnSurfaceVariantLight
import com.kouta.design.resource.OnTertiaryContainerDark
import com.kouta.design.resource.OnTertiaryContainerLight
import com.kouta.design.resource.OnTertiaryDark
import com.kouta.design.resource.OnTertiaryLight
import com.kouta.design.resource.OutlineDark
import com.kouta.design.resource.OutlineLight
import com.kouta.design.resource.OutlineVariantDark
import com.kouta.design.resource.OutlineVariantLight
import com.kouta.design.resource.PrimaryContainerDark
import com.kouta.design.resource.PrimaryContainerLight
import com.kouta.design.resource.PrimaryDark
import com.kouta.design.resource.PrimaryLight
import com.kouta.design.resource.ScrimDark
import com.kouta.design.resource.ScrimLight
import com.kouta.design.resource.SecondaryContainerDark
import com.kouta.design.resource.SecondaryContainerLight
import com.kouta.design.resource.SecondaryDark
import com.kouta.design.resource.SecondaryLight
import com.kouta.design.resource.SurfaceDark
import com.kouta.design.resource.SurfaceLight
import com.kouta.design.resource.SurfaceTintDark
import com.kouta.design.resource.SurfaceTintLight
import com.kouta.design.resource.SurfaceVariantDark
import com.kouta.design.resource.SurfaceVariantLight
import com.kouta.design.resource.TertiaryContainerDark
import com.kouta.design.resource.TertiaryContainerLight
import com.kouta.design.resource.TertiaryDark
import com.kouta.design.resource.TertiaryLight
import com.kouta.design.resource.Typography

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    inversePrimary = InversePrimaryDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    surfaceTint = SurfaceTintDark,
    inverseSurface = InverseSurfaceDark,
    inverseOnSurface = InverseOnSurfaceDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
    scrim = ScrimDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    inversePrimary = InversePrimaryLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    surfaceTint = SurfaceTintLight,
    inverseSurface = InverseSurfaceLight,
    inverseOnSurface = InverseOnSurfaceLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,
    scrim = ScrimLight
)

@Composable
fun YoutubeAnalyzeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}