package com.example.mixtapp.ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.TIPO_ARTISTA
import com.example.mixtapp.data.model.toItemResumenUi
import com.example.mixtapp.ui.theme.MixtappTheme

// Un album, una cancion o un artista en una fila: la imagen y debajo sus lineas, en tres niveles: el
// titulo (hasta dos lineas, para que no se corte tan pronto), el subtitulo y el detalle mas tenue.
// Los albumes y las canciones van con la portada cuadrada y los artistas con la foto redonda, asi se
// distingue de un vistazo que tipo es
@Composable
fun ItemCard(
    titulo: String,
    subtitulo: String,
    detalle: String,
    imagenUrl: String,
    // true en los artistas: la foto va redonda y los textos centrados
    circular: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tamano: Dp = 140.dp,
) {
    val alineacion = if (circular) TextAlign.Center else TextAlign.Start

    Column(
        modifier = modifier
            .width(tamano)
            .rebote()
            .clickable { onClick() }
    ) {
        ItemAsyncImage(
            imagenUrl = imagenUrl,
            titulo = titulo,
            esquinas = if (circular) tamano / 2 else 20.dp,
            modifier = Modifier.size(tamano)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = titulo,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleSmall,
            textAlign = alineacion,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(tamano),
        )
        Text(
            text = subtitulo,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            textAlign = alineacion,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(tamano),
        )
        if (detalle.isNotEmpty()) {
            Text(
                text = detalle,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                style = MaterialTheme.typography.labelSmall,
                textAlign = alineacion,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.width(tamano),
            )
        }
    }
}

// Una fila con scroll horizontal de items guardados o favoritos (solo traen la copia del item)
@Composable
fun ItemResumenRow(
    items: List<ItemResumenUi>,
    onItemClick: (ItemResumenUi) -> Unit,
    modifier: Modifier = Modifier,
    tamano: Dp = 120.dp,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(items) { item ->
            ItemCard(
                titulo = item.titulo,
                subtitulo = item.subtitulo,
                detalle = "",
                imagenUrl = item.imagenUrl,
                circular = item.tipo == TIPO_ARTISTA,
                onClick = { onItemClick(item) },
                tamano = tamano,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ItemCardPreview() {
    MixtappTheme(darkTheme = true) {
        val cancion = LocalPreviewProvider.emptinessMachine

        ItemCard(
            titulo = cancion.titulo,
            subtitulo = cancion.subtitulo,
            detalle = cancion.detalle,
            imagenUrl = cancion.imagenUrl,
            circular = false,
            onClick = {},
            modifier = Modifier.padding(8.dp),
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ItemCardArtistaPreview() {
    MixtappTheme(darkTheme = true) {
        val artista = LocalPreviewProvider.mikeShinoda

        ItemCard(
            titulo = artista.titulo,
            subtitulo = artista.subtitulo,
            detalle = artista.detalle,
            imagenUrl = artista.imagenUrl,
            circular = true,
            onClick = {},
            modifier = Modifier.padding(8.dp),
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ItemResumenRowPreview() {
    MixtappTheme(darkTheme = true) {
        ItemResumenRow(
            items = LocalPreviewProvider.favoritos + LocalPreviewProvider.karolG.toItemResumenUi(),
            onItemClick = {},
        )
    }
}
