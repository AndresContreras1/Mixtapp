package com.example.mixtapp.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme

@Composable
fun ProfileAsyncImage(
    profileImage: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(profileImage)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        error = painterResource(id = R.drawable.user_image_icon),
        placeholder = painterResource(id = R.drawable.loading_img),
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}


@Composable
@Preview
fun ProfileAsyncImagePreview() {
    MixtappTheme(darkTheme = true, dynamicColor = false) {
        ProfileAsyncImage(
            profileImage = "",
            contentDescription = "",
            modifier = Modifier.size(46.dp)
        )
    }
}
