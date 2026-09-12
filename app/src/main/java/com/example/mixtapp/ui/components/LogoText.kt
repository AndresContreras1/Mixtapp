package com.example.mixtapp.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import com.example.mixtapp.R

@Composable
fun LogoText(
    fontSize: TextUnit,
    fontWeight: FontWeight,
    lineHeight: TextUnit = TextUnit.Unspecified,
    modifier: Modifier = Modifier
) {
    val nombreClaro = stringResource(R.string.logo_mixt)
    val nombreRosa = stringResource(R.string.logo_app)

    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                append(nombreClaro)
            }
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.tertiary)) {
                append(nombreRosa)
            }
        },
        modifier = modifier,
        fontSize = fontSize,
        lineHeight = lineHeight,
        fontWeight = fontWeight,
        fontFamily = FontFamily.Serif
    )
}
