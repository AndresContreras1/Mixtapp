package com.example.mixtapp.ui.screens.review.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.theme.CircleBerry
import com.example.mixtapp.ui.theme.CircleWine

@Composable
fun WriteReviewBackground(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        ReviewCircleDecorations()
    }
}

@Composable
private fun BoxScope.ReviewCircleDecorations() {
    Box(
        modifier = Modifier
            .size(210.dp)
            .align(Alignment.TopEnd)
            .offset(x = 72.dp, y = (-40).dp)
            .clip(CircleShape)
            .background(CircleWine.copy(alpha = 0.48f))
    )

    Box(
        modifier = Modifier
            .size(270.dp)
            .align(Alignment.BottomStart)
            .offset(x = (-130).dp, y = (-52).dp)
            .clip(CircleShape)
            .background(CircleBerry.copy(alpha = 0.28f))
    )

    Box(
        modifier = Modifier
            .size(176.dp)
            .align(Alignment.CenterEnd)
            .offset(x = 104.dp, y = (-180).dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.14f))
    )
}
