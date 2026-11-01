package com.example.mixtapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Modo oscuro: el fondo clasico de vino casi negro, con los tonos pastel (100) en los botones,
// los textos destacados y los circulos
private val DarkColorScheme = darkColorScheme(
    primary = Crimson100,
    onPrimary = Crimson600,
    primaryContainer = Crimson400,
    onPrimaryContainer = Crimson50,
    inversePrimary = Crimson400,

    secondary = Mauve100,
    onSecondary = Mauve600,
    secondaryContainer = Mauve400,
    onSecondaryContainer = Mauve50,

    tertiary = Plum100,
    onTertiary = Plum600,
    tertiaryContainer = Plum500,
    onTertiaryContainer = Plum50,

    background = Mauve600,
    onBackground = Grey50,

    surface = Mauve600,
    onSurface = Grey50,
    surfaceVariant = Plum600,
    onSurfaceVariant = Mauve100,
    surfaceTint = Crimson100,
    inverseSurface = Mauve50,
    inverseOnSurface = Mauve600,

    surfaceDim = Crimson600,
    surfaceBright = Plum500,
    surfaceContainerLowest = Grey600,
    surfaceContainerLow = Crimson600,
    surfaceContainer = Plum600,
    surfaceContainerHigh = Mauve500,
    surfaceContainerHighest = Plum500,

    error = Crimson200,
    onError = Crimson600,
    errorContainer = Crimson500,
    onErrorContainer = Crimson50,

    outline = Mauve300,
    outlineVariant = Mauve500,
    scrim = Black,
)

// Modo claro: fondos pastel (50 y 100) y los tonos medios (400) para que el texto y los botones se
// lean bien
private val LightColorScheme = lightColorScheme(
    primary = Crimson400,
    onPrimary = White,
    primaryContainer = Crimson100,
    onPrimaryContainer = Crimson600,
    inversePrimary = Crimson100,

    secondary = Mauve400,
    onSecondary = White,
    secondaryContainer = Mauve100,
    onSecondaryContainer = Mauve600,

    tertiary = Plum400,
    onTertiary = White,
    tertiaryContainer = Plum100,
    onTertiaryContainer = Plum600,

    background = Plum50,
    onBackground = Grey600,

    surface = Plum50,
    onSurface = Grey600,
    surfaceVariant = Mauve50,
    onSurfaceVariant = Grey400,
    surfaceTint = Crimson400,
    inverseSurface = Mauve600,
    inverseOnSurface = Mauve50,

    surfaceDim = Mauve100,
    surfaceBright = White,
    surfaceContainerLowest = White,
    surfaceContainerLow = Plum50,
    surfaceContainer = Mauve50,
    surfaceContainerHigh = Plum100,
    surfaceContainerHighest = Mauve100,

    error = Crimson300,
    onError = White,
    errorContainer = Crimson50,
    onErrorContainer = Crimson600,

    outline = Mauve200,
    outlineVariant = Mauve100,
    scrim = Black,
)

// El dynamic color queda apagado: la app tiene su propia paleta y no toma los colores del fondo
// de pantalla del celular
@Composable
fun MixtappTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
