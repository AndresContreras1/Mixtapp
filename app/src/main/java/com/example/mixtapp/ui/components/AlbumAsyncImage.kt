package com.example.mixtapp.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mixtapp.R
import com.example.mixtapp.data.local.AlbumCovers
import com.example.mixtapp.ui.theme.MixtappTheme

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


@Composable
@Preview
fun AlbumAsyncImagePreview() {
    MixtappTheme(darkTheme = true, dynamicColor = false) {
        AlbumAsyncImage(
            cover = AlbumCovers.TEATRO_DIRA,
            contentDescription = "",
            alpha = 1f,
            modifier = Modifier.size(120.dp)
        )
    }
}
