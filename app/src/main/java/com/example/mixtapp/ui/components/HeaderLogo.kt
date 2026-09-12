package com.example.mixtapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.LogoCircle

@Composable
fun HeaderLogo(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
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
            fontSize = 47.sp,
            lineHeight = 52.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Serif
        )

        Box(
            modifier = Modifier
                .size(66.dp)
                .clip(CircleShape)
                .background(LogoCircle),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder for user avatar or similar
        }
    }
}