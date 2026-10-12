package com.example.mixtapp.ui.screens.following.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

// Los circulos se hacen como en AppBackground: un Box redondo con fondo
@Composable
fun FollowingBackground(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(312.dp)
                .offset(x = (-228).dp, y = 204.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.58f))
        )

        Box(
            modifier = Modifier
                .size(264.dp)
                .align(Alignment.TopEnd)
                .offset(x = 174.dp, y = 428.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceDim.copy(alpha = 0.9f))
        )
    }
}
