package com.example.mixtapp.ui.screens.following.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun FollowingStatusIcons(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            listOf(9.dp, 13.dp, 17.dp).forEach { barHeight ->
                Canvas(modifier = Modifier.size(width = 4.dp, height = barHeight)) {
                    drawRoundRect(color = Color.White, cornerRadius = CornerRadius(2.dp.toPx()))
                }
            }
        }

        Canvas(modifier = Modifier.size(width = 39.dp, height = 18.dp)) {
            drawRoundRect(
                color = Color.White,
                topLeft = Offset.Zero,
                size = Size(35.dp.toPx(), 15.dp.toPx()),
                cornerRadius = CornerRadius(5.dp.toPx()),
                style = Stroke(width = 2.6.dp.toPx()),
            )
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(5.dp.toPx(), 4.dp.toPx()),
                size = Size(24.dp.toPx(), 7.dp.toPx()),
                cornerRadius = CornerRadius(2.dp.toPx()),
            )
        }
    }
}
