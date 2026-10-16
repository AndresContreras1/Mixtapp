package com.example.mixtapp.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.theme.MixtappTheme
import androidx.compose.ui.graphics.Brush
import com.example.mixtapp.ui.theme.BackgroundGlow

@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(MaterialTheme.colorScheme.background, BackgroundGlow)
                )
            )
    ) {
        DecorativeCircles()
    }
}

@Composable
fun DecorativeCircles(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        DecorativeCircle(
            size = 240.dp,
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.58f),
            modifier = Modifier.offset(x = (-114).dp, y = (-38).dp)
        )

        DecorativeCircle(
            size = 228.dp,
            color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.11f),
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 32.dp)
        )

        DecorativeCircle(
            size = 275.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.33f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 112.dp, y = (-24).dp)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun AppBackgroundPreview() {
    MixtappTheme(darkTheme = true) {
        AppBackground()
    }
}
