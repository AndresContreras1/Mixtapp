package com.example.mixtapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryPink,
    onPrimary = Color.White,
    primaryContainer = FieldBackground,
    onPrimaryContainer = PalePink,
    inversePrimary = LinkPink,

    secondary = CircleWine,
    onSecondary = Color.White,
    secondaryContainer = CircleWine,
    onSecondaryContainer = PalePink,

    tertiary = LogoPink,
    onTertiary = Color.White,
    tertiaryContainer = SurfaceCard,
    onTertiaryContainer = PalePink,

    background = DeepBackground,
    onBackground = TextPink,

    surface = DeepBackground,
    onSurface = Color.White,
    surfaceVariant = FieldBackground,
    onSurfaceVariant = PalePink,
    surfaceTint = PrimaryPink,
    inverseSurface = PalePink,
    inverseOnSurface = DeepBackground,

    surfaceDim = FollowingOverlay,
    surfaceBright = CircleWine,
    surfaceContainerLowest = CardBackground,
    surfaceContainerLow = DeepBackground,
    surfaceContainer = SurfaceCard,
    surfaceContainerHigh = FieldBackground,
    surfaceContainerHighest = CircleWine,

    error = PrimaryPink,
    onError = Color.White,
    errorContainer = FieldBackground,
    onErrorContainer = PalePink,

    outline = FieldBorder,
    outlineVariant = DividerWine,
    scrim = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryPink,
    onPrimary = Color.White,
    primaryContainer = FieldBackground,
    onPrimaryContainer = PalePink,
    inversePrimary = LinkPink,

    secondary = CircleWine,
    onSecondary = Color.White,
    secondaryContainer = CircleWine,
    onSecondaryContainer = PalePink,

    tertiary = LogoPink,
    onTertiary = Color.White,
    tertiaryContainer = SurfaceCard,
    onTertiaryContainer = PalePink,

    background = DeepBackground,
    onBackground = TextPink,

    surface = DeepBackground,
    onSurface = Color.White,
    surfaceVariant = FieldBackground,
    onSurfaceVariant = PalePink,
    surfaceTint = PrimaryPink,
    inverseSurface = PalePink,
    inverseOnSurface = DeepBackground,

    surfaceDim = FollowingOverlay,
    surfaceBright = CircleWine,
    surfaceContainerLowest = CardBackground,
    surfaceContainerLow = DeepBackground,
    surfaceContainer = SurfaceCard,
    surfaceContainerHigh = FieldBackground,
    surfaceContainerHighest = CircleWine,

    error = PrimaryPink,
    onError = Color.White,
    errorContainer = FieldBackground,
    onErrorContainer = PalePink,

    outline = FieldBorder,
    outlineVariant = DividerWine,
    scrim = Color.Black
)

@Composable
fun MixtappTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
