package com.example.mixtapp.ui.utils

import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mixtapp.R

// La portada grande del detalle, igual que ItemAsyncImage, que ademas avisa el color mas vivo de la
// imagen con Palette. Ese color solo pinta el degradado decorativo de arriba: los textos y el resto
// del fondo no cambian
@Composable
fun PortadaConColor(
    imagenUrl: String,
    titulo: String,
    esquinas: Dp,
    onColor: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imagenUrl)
            .crossfade(true)
            // Palette necesita leer los pixeles: la imagen no puede quedar solo en la GPU
            .allowHardware(false)
            .build(),
        contentDescription = stringResource(R.string.portada_de, titulo),
        error = painterResource(id = R.drawable.ic_broken_image),
        placeholder = painterResource(id = R.drawable.loading_img),
        contentScale = ContentScale.Crop,
        modifier = modifier.clip(RoundedCornerShape(esquinas)),
        onSuccess = { exito ->
            val bitmap = (exito.result.drawable as? BitmapDrawable)?.bitmap
            if (bitmap != null) {
                // generate con listener saca el color en otro hilo y responde en el principal
                Palette.from(bitmap).generate { paleta ->
                    val muestra = paleta?.vibrantSwatch ?: paleta?.dominantSwatch
                    if (muestra != null) onColor(Color(muestra.rgb))
                }
            }
        },
    )
}
