package com.example.mixtapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.theme.MixtappTheme

@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        DecorativeCircles()
    }
}

@Composable
fun BoxScope.DecorativeCircles() {
    Box(
        modifier = Modifier
            .size(240.dp)
            .offset(x = (-114).dp, y = (-38).dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.58f))
    )

    Box(
        modifier = Modifier
            .size(228.dp)
            .align(Alignment.Center)
            .offset(y = 32.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.11f))
    )
    Box(
        modifier = Modifier
            .size(275.dp)
            .align(Alignment.BottomEnd)
            .offset(x = 112.dp, y = (-24).dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.33f))
    )
}

@Composable
@Preview(showBackground = true)
fun DecorativeCirclesPreview() {
    MixtappTheme(darkTheme = true, dynamicColor = false) {
        AppBackground()
    }
}
