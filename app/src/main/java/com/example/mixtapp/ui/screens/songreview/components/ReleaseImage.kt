package com.example.mixtapp.ui.screens.songreview.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun ReleaseImage(
    coverRes: Int,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = coverRes),
        contentDescription = null,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp)),
        contentScale = ContentScale.Crop
    )
}
