package com.example.mixtapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mixtapp.R

@Composable
fun AlbumAsyncImage(
    cover: String,
    contentDescription: String,
    alpha: Float,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(cover)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        error = painterResource(id = R.drawable.ic_broken_image),
        placeholder = painterResource(id = R.drawable.loading_img),
        contentScale = ContentScale.Crop,
        alpha = alpha,
        modifier = modifier
    )
}
