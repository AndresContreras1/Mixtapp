package com.example.mixtapp.ui.screens.review.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.utils.DecorativeCircle

@Composable
fun WriteReviewBackground(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        ReviewCircleDecorations()
    }
}

@Composable
private fun ReviewCircleDecorations(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        DecorativeCircle(
            size = 210.dp,
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.48f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 72.dp, y = (-40).dp)
        )

        DecorativeCircle(
            size = 270.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-130).dp, y = (-52).dp)
        )

        DecorativeCircle(
            size = 176.dp,
            color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.14f),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 104.dp, y = (-180).dp)
        )
    }
}
