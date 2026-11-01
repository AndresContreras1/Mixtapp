package com.example.mixtapp.ui.utils

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.ui.theme.MixtappTheme

// La portada de un album o una cancion, o la foto de un artista, con las esquinas redondeadas.
// Coil la descarga de la URL; mientras llega se ve la de carga y si falla, la de imagen rota
@Composable
fun ItemAsyncImage(
    imagenUrl: String,
    titulo: String,
    modifier: Modifier = Modifier,
    esquinas: Dp = 20.dp,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imagenUrl)
            .crossfade(true)
            .build(),
        contentDescription = stringResource(R.string.album_cover, titulo),
        error = painterResource(id = R.drawable.ic_broken_image),
        placeholder = painterResource(id = R.drawable.loading_img),
        contentScale = ContentScale.Crop,
        modifier = modifier.clip(RoundedCornerShape(esquinas))
    )
}

@Composable
@Preview(showBackground = true)
fun ItemAsyncImagePreview() {
    MixtappTheme(darkTheme = true) {
        ItemAsyncImage(
            imagenUrl = LocalPreviewProvider.fromZero.imagenUrl,
            titulo = LocalPreviewProvider.fromZero.titulo,
            modifier = Modifier.size(140.dp)
        )
    }
}
