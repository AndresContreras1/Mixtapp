package com.example.mixtapp.ui.screens.mixtapes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.MixtapeUi
import com.example.mixtapp.data.model.toItemResumenUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.ItemAsyncImage

// Con cuatro items o mas la portada es un mosaico de cuatro
private const val PORTADAS_DEL_MOSAICO = 4

// Una mixtape en una lista: su portada, el nombre, la descripcion y cuantos items tiene. Tocarla
// abre su detalle. La etiqueta del final la usa "Agregar a una mixtape" para decir "Ya esta"
@Composable
fun MixtapeCard(
    mixtape: MixtapeUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    etiqueta: String? = null,
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .testTag("mixtape_${mixtape.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f)
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PortadaDeMixtape(items = mixtape.items)

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mixtape.nombre,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                if (mixtape.descripcion.isNotEmpty()) {
                    Text(
                        text = mixtape.descripcion,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (mixtape.itemCount == 1) {
                        stringResource(R.string.un_elemento_mixtape)
                    } else {
                        stringResource(R.string.elementos_mixtape, mixtape.itemCount)
                    },
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            if (etiqueta != null) {
                Text(
                    text = etiqueta,
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

// La portada: vacia lleva el icono; con menos de cuatro items, la del primero; con cuatro o mas,
// un mosaico con las cuatro primeras
@Composable
private fun PortadaDeMixtape(
    items: List<ItemResumenUi>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(72.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        when {
            items.isEmpty() -> Icon(
                imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(36.dp),
            )
            items.size < PORTADAS_DEL_MOSAICO -> ItemAsyncImage(
                imagenUrl = items.first().imagenUrl,
                titulo = items.first().titulo,
                esquinas = 0.dp,
                modifier = Modifier.fillMaxSize(),
            )
            // Dos filas de dos: cada portada ocupa la mitad con weight
            else -> Column(modifier = Modifier.fillMaxSize()) {
                items.take(PORTADAS_DEL_MOSAICO).chunked(2).forEach { fila ->
                    Row(modifier = Modifier.weight(1f)) {
                        fila.forEach { item ->
                            ItemAsyncImage(
                                imagenUrl = item.imagenUrl,
                                titulo = item.titulo,
                                esquinas = 0.dp,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize(),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MixtapeCardPreview() {
    MixtappTheme(darkTheme = true) {
        MixtapeCard(
            mixtape = MixtapeUi(
                id = "mixtape_1",
                nombre = "Para entrenar",
                descripcion = "Lo que suena en el gimnasio",
                usuarioId = "yo",
                usuarioNombre = "Andres",
                usuarioFotoUrl = "",
                items = listOf(LocalPreviewProvider.fromZero.toItemResumenUi(), LocalPreviewProvider.karolG.toItemResumenUi()),
                isMine = true,
                timeAgo = "2 d",
            ),
            onClick = {},
        )
    }
}
