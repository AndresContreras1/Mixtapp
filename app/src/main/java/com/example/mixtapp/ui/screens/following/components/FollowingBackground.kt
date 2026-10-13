package com.example.mixtapp.ui.screens.following.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.DecorativeCircle

// Los mismos circulos de AppBackground, en otras posiciones
@Composable
fun FollowingBackground(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        DecorativeCircle(
            size = 312.dp,
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.58f),
            modifier = Modifier.offset(x = (-228).dp, y = 204.dp)
        )

        DecorativeCircle(
            size = 264.dp,
            color = MaterialTheme.colorScheme.surfaceDim.copy(alpha = 0.9f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 174.dp, y = 428.dp)
        )
    }
}

@Composable
@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
fun FollowingBackgroundPreview() {
    MixtappTheme(darkTheme = true) {
        FollowingBackground()
    }
}
