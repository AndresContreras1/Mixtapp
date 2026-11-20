package com.example.mixtapp.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.EstadoDeReproduccion
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.ui.theme.MixtappTheme

// La barra flotante de lo que suena: la portada, el titulo, el artista y el play, que mientras
// suena muestra el ecualizador y se toca para detenerlo. Va abajo de la pantalla mientras suena un fragmento, como en las apps de musica
@Composable
fun MiniReproductor(
    cancion: ItemUi,
    estado: EstadoDeReproduccion?,
    onClick: () -> Unit,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val forma = RoundedCornerShape(22.dp)

    // Solida y con sombra: flota sobre la lista sin que se vea lo de atras
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 14.dp, shape = forma)
            .clip(forma)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .clickable { onClick() }
            .padding(10.dp)
            .testTag("miniReproductor"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ItemAsyncImage(imagenUrl = cancion.imagenUrl, titulo = cancion.titulo, esquinas = 12.dp, modifier = Modifier.size(48.dp))

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = cancion.titulo,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = cancion.artista,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        BotonDeVistaPrevia(estado = estado, titulo = cancion.titulo, tamano = 42.dp, onClick = onPlayClick)
    }
}

@Composable
@Preview(showBackground = true)
fun MiniReproductorPreview() {
    MixtappTheme(darkTheme = true) {
        MiniReproductor(
            cancion = LocalPreviewProvider.emptinessMachine,
            estado = EstadoDeReproduccion.Sonando,
            onClick = {},
            onPlayClick = {},
        )
    }
}
