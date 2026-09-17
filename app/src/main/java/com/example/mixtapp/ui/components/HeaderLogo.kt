package com.example.mixtapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.ui.theme.MixtappTheme

@Composable
fun HeaderLogo(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        LogoText(
            fontSize = 47.sp,
            fontWeight = FontWeight.Black,
            lineHeight = 52.sp
        )

        Box(
            modifier = Modifier
                .size(66.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder for user avatar or similar
        }
    }
}


@Composable
@Preview
fun HeaderLogoPreview() {
    MixtappTheme(darkTheme = true, dynamicColor = false) {
        HeaderLogo()
    }
}
