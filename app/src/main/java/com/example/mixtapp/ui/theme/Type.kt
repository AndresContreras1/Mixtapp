package com.example.mixtapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R

// Las fuentes se descargan de Google Fonts: el certificado esta en res/values/font_certs.xml.
// Si no se pueden descargar (sin internet), Android pone la fuente por defecto
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val outfit = GoogleFont("Outfit")

// Una sola letra en toda la app, redondeada. La jerarquia sale del tamano y del grosor: por eso
// la familia trae del normal al Black, y cada estilo de abajo elige el suyo
val appFontFamily = FontFamily(
    Font(googleFont = outfit, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = outfit, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = outfit, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = outfit, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = outfit, fontProvider = provider, weight = FontWeight.ExtraBold),
    Font(googleFont = outfit, fontProvider = provider, weight = FontWeight.Black),
)

// Los estilos de Material 3 por defecto: se copian con la familia, el tamano y el grosor de cada
// nivel
val baseline = Typography()

// Del mas grande al mas pequeno:
// - display: los numeros grandes de Tu ano y las cifras que se quieren mostrar
// - headline: los titulos de las pantallas y del item, en negrita
// - title: las secciones y los titulos de las tarjetas, semibold
// - body: el texto que se lee
// - label: los botones (semibold) y los metadatos (medium, pequenos)
val Typography = Typography(
    displayLarge = baseline.displayLarge.copy(
        fontFamily = appFontFamily, fontWeight = FontWeight.Black, fontSize = 56.sp, lineHeight = 60.sp,
    ),
    displayMedium = baseline.displayMedium.copy(
        fontFamily = appFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 44.sp, lineHeight = 48.sp,
    ),
    displaySmall = baseline.displaySmall.copy(
        fontFamily = appFontFamily, fontWeight = FontWeight.Bold, fontSize = 34.sp, lineHeight = 40.sp,
    ),
    headlineLarge = baseline.headlineLarge.copy(
        fontFamily = appFontFamily, fontWeight = FontWeight.Bold, fontSize = 30.sp, lineHeight = 36.sp,
    ),
    headlineMedium = baseline.headlineMedium.copy(
        fontFamily = appFontFamily, fontWeight = FontWeight.Bold, fontSize = 26.sp, lineHeight = 32.sp,
    ),
    headlineSmall = baseline.headlineSmall.copy(
        fontFamily = appFontFamily, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp,
    ),
    titleLarge = baseline.titleLarge.copy(
        fontFamily = appFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 26.sp,
    ),
    titleMedium = baseline.titleMedium.copy(
        fontFamily = appFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp,
    ),
    titleSmall = baseline.titleSmall.copy(
        fontFamily = appFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp,
    ),
    bodyLarge = baseline.bodyLarge.copy(
        fontFamily = appFontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp,
    ),
    bodyMedium = baseline.bodyMedium.copy(
        fontFamily = appFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp,
    ),
    bodySmall = baseline.bodySmall.copy(
        fontFamily = appFontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp,
    ),
    labelLarge = baseline.labelLarge.copy(
        fontFamily = appFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp,
    ),
    labelMedium = baseline.labelMedium.copy(
        fontFamily = appFontFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp,
    ),
    labelSmall = baseline.labelSmall.copy(
        fontFamily = appFontFamily, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp,
    ),
)
