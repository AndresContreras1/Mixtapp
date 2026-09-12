package com.example.mixtapp.ui.screens.review.components

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

@Composable
fun WriteReviewBackground(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        ReviewCircleDecorations()
    }
}

@Composable
private fun ReviewCircleDecorations(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(210.dp)
                .align(Alignment.TopEnd)
                .offset(x = 72.dp, y = (-40).dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.48f))
        )

        Box(
            modifier = Modifier
                .size(270.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-130).dp, y = (-52).dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.28f))
        )

        Box(
            modifier = Modifier
                .size(176.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 104.dp, y = (-180).dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.14f))
        )
    }
}
