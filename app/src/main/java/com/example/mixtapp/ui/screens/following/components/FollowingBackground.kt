package com.example.mixtapp.ui.screens.following.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.theme.CircleWine

@Composable
fun FollowingBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawCircle(
            color = CircleWine.copy(alpha = 0.58f),
            radius = 156.dp.toPx(),
            center = Offset(-72.dp.toPx(), 360.dp.toPx()),
        )
        drawCircle(
            color = Color(0xFF3C061E).copy(alpha = 0.9f),
            radius = 132.dp.toPx(),
            center = Offset(size.width + 42.dp.toPx(), 560.dp.toPx()),
        )
    }
}
