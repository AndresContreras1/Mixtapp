package com.example.mixtapp.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.theme.MixtappTheme

// Un circulo de color para los fondos; quien lo usa lo ubica con align y offset en el modifier
@Composable
fun DecorativeCircle(
    size: Dp,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
@Preview(showBackground = true)
fun DecorativeCirclePreview() {
    MixtappTheme(darkTheme = true) {
        DecorativeCircle(size = 120.dp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.33f))
    }
}
