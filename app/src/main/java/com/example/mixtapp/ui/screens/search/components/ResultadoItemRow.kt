package com.example.mixtapp.ui.screens.search.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemConResenasUi
import com.example.mixtapp.data.model.TIPO_ARTISTA
import com.example.mixtapp.data.model.TIPO_CANCION
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.ItemAsyncImage
import com.example.mixtapp.ui.utils.StarRating

// Un album, una cancion o un artista en una lista: la imagen, sus lineas, que tipo es y el
// promedio de estrellas. La usan Buscar y Elegir que calificar; cada pantalla le pone su testTag
@Composable
fun ResultadoItemRow(
    itemConResenas: ItemConResenasUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val item = itemConResenas.item

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.7f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ItemAsyncImage(
                imagenUrl = item.imagenUrl,
                titulo = item.titulo,
                esquinas = 14.dp,
                modifier = Modifier.size(64.dp),
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.titulo,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = item.subtitulo,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
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

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(etiquetaDelTipo(tipo = item.tipo)),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    StarRating(rating = itemConResenas.estrellas, starSize = 14.dp)
                }
            }
        }
    }
}

// El nombre del tipo que se muestra en la etiqueta
@StringRes
private fun etiquetaDelTipo(tipo: String): Int = when (tipo) {
    TIPO_CANCION -> R.string.tipo_cancion
    TIPO_ARTISTA -> R.string.tipo_artista
    else -> R.string.tipo_album
}

@Composable
@Preview(showBackground = true)
fun ResultadoItemRowPreview() {
    MixtappTheme(darkTheme = true) {
        ResultadoItemRow(
            itemConResenas = LocalPreviewProvider.itemConResenas.copy(item = LocalPreviewProvider.emptinessMachine),
            onClick = {},
        )
    }
}
