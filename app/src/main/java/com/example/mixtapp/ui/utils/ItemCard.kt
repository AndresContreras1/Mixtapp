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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.toItemResumenUi
import com.example.mixtapp.ui.theme.MixtappTheme

// Un album, una cancion o un artista en una fila: la imagen y debajo sus lineas. La cancion lleva
// tres (titulo, album y artista); el album y el artista, dos
@Composable
fun ItemCard(
    titulo: String,
    subtitulo: String,
    detalle: String,
    imagenUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tamano: Dp = 140.dp,
) {
    Column(
        modifier = modifier
            .width(tamano)
            .clickable { onClick() }
    ) {
        ItemAsyncImage(
            imagenUrl = imagenUrl,
            titulo = titulo,
            modifier = Modifier.size(tamano)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = titulo,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = subtitulo,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (detalle.isNotEmpty()) {
            Text(
                text = detalle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        items(items) { item ->
            ItemCard(
                titulo = item.titulo,
                subtitulo = item.subtitulo,
                detalle = "",
                imagenUrl = item.imagenUrl,
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
