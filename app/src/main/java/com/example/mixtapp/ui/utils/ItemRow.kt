package com.example.mixtapp.ui.utils

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.TIPO_ARTISTA
import com.example.mixtapp.data.model.TIPO_CANCION
import com.example.mixtapp.ui.theme.MixtappTheme

// Un album, una cancion o un artista en una lista, en tres niveles: el titulo (hasta dos lineas), el
// album o el artista, y abajo los metadatos tenues: la etiqueta de que tipo es y lo que cada lista
// necesite (la calificacion de la comunidad en Buscar y en Elegir que calificar, la fecha en los
// lanzamientos). Toda la tarjeta se puede tocar y la flecha del final lo indica. Cada pantalla le pone
// su testTag
@Composable
fun ItemRow(
    item: ItemUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    metadatos: @Composable () -> Unit,
) {
    TarjetaTranslucida(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ItemAsyncImage(
                imagenUrl = item.imagenUrl,
                titulo = item.titulo,
                esquinas = if (item.tipo == TIPO_ARTISTA) 40.dp else 14.dp,
                modifier = Modifier.size(76.dp),
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.titulo,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = item.subtitulo,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                // Solo la cancion tiene detalle (su artista)
                if (item.detalle.isNotEmpty()) {
                    Text(
                        text = item.detalle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(etiquetaDelTipo(tipo = item.tipo)),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    metadatos()
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// La calificacion de la comunidad como texto: "4.5 ★ · 24 reseñas". Sin reseñas dice "Sin reseñas"
// en vez de cinco estrellas vacias, que se confunden con una mala calificacion
@Composable
fun CalificacionDeLaComunidad(
    rating: Double,
    ratingCount: Int,
    modifier: Modifier = Modifier,
) {
    if (ratingCount == 0) {
        Text(
            text = stringResource(R.string.sin_resenas_corto),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
            modifier = modifier,
        )
        return
    }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(R.string.rating_promedio, rating),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium,
        )
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .size(14.dp),
        )
        Text(
            text = stringResource(
                if (ratingCount == 1) R.string.una_resena else R.string.cantidad_resenas,
                ratingCount,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

// Album, cancion o artista, para la etiqueta
@StringRes
private fun etiquetaDelTipo(tipo: String): Int = when (tipo) {
    TIPO_CANCION -> R.string.tipo_cancion
    TIPO_ARTISTA -> R.string.tipo_artista
    else -> R.string.tipo_album
}

@Composable
@Preview(showBackground = true)
fun ItemRowPreview() {
    MixtappTheme(darkTheme = true) {
        ItemRow(
            item = LocalPreviewProvider.emptinessMachine,
            onClick = {},
        ) {
            CalificacionDeLaComunidad(rating = 4.5, ratingCount = 24)
        }
    }
}
