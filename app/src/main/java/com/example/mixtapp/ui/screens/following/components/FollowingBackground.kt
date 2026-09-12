package com.example.mixtapp.ui.screens.following.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp

@Composable
fun FollowingBackground(modifier: Modifier = Modifier) {
    val circuloIzquierdo = MaterialTheme.colorScheme.secondary.copy(alpha = 0.58f)
    val circuloDerecho = MaterialTheme.colorScheme.surfaceDim.copy(alpha = 0.9f)

    Canvas(modifier = modifier.fillMaxSize()) {
        drawCircle(
            color = circuloIzquierdo,
            radius = 156.dp.toPx(),
            center = Offset(-72.dp.toPx(), 360.dp.toPx()),
        )
        drawCircle(
            color = circuloDerecho,
            radius = 132.dp.toPx(),
            center = Offset(size.width + 42.dp.toPx(), 560.dp.toPx()),
        )
    }
}
