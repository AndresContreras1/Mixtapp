package com.example.mixtapp.ui.screens.songreview.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.components.AlbumAsyncImage

@Composable
fun ReleaseImage(
    cover: String,
    albumTitle: String,
    modifier: Modifier = Modifier
) {
    AlbumAsyncImage(
        cover = cover,
        contentDescription = stringResource(R.string.album_cover, albumTitle),
        alpha = 1f,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
    )
}
