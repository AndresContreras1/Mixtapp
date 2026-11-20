package com.example.mixtapp.ui.screens.duelos.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.toItemResumenUi
import com.example.mixtapp.ui.theme.Black
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.theme.White
import com.example.mixtapp.ui.utils.ItemAsyncImage
import com.example.mixtapp.ui.utils.rebote

// Uno de los dos del duelo: la portada, el titulo con peso y, si ya hay votos, su porcentaje y
// cuantos lleva. Tocar la tarjeta lo elige para votar: el borde rosado, el check y la elevacion lo
// marcan. El detalle se abre con el icono de informacion de la esquina, para que no se confunda con
// elegir. El trofeo marca al que va ganando y "Tu voto" al que vote
@Composable
fun DueloItemCard(
    item: ItemResumenUi,
    porcentaje: Int,
    votos: Int,
    // Sin votos no se muestran porcentajes: la pantalla invita a votar
    sinVotos: Boolean,
    elegido: Boolean,
    esMiVoto: Boolean,
    vaGanando: Boolean,
    onClick: () -> Unit,
    onVerDetalle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.rebote(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (elegido) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
            } else {
                MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.75f)
            }
        ),
        border = if (elegido) BorderStroke(3.dp, MaterialTheme.colorScheme.primary) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (elegido) 10.dp else 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box {
                ItemAsyncImage(
                    imagenUrl = item.imagenUrl,
                    titulo = item.titulo,
                    esquinas = 18.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                )

                // Ver su detalle, aparte de elegirlo
                IconButton(
                    onClick = onVerDetalle,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Black.copy(alpha = 0.45f)),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = stringResource(R.string.ver_detalle),
                        tint = White,
                        modifier = Modifier.size(20.dp),
                    )
                }

                if (elegido) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = stringResource(R.string.elegido),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(White),
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.titulo,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = item.subtitulo,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            if (!sinVotos) {
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (vaGanando) {
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = stringResource(R.string.va_ganando),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = stringResource(R.string.porcentaje, porcentaje),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                }
                Text(
                    text = stringResource(R.string.cantidad_votos, votos),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                )
            }

            if (esMiVoto) {
                Text(
                    text = stringResource(R.string.tu_voto),
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun DueloItemCardPreview() {
    MixtappTheme(darkTheme = true) {
        DueloItemCard(
            item = LocalPreviewProvider.fromZero.toItemResumenUi(),
            porcentaje = 64,
            votos = 16,
            sinVotos = false,
            elegido = true,
            esMiVoto = true,
            vaGanando = true,
            onClick = {},
            onVerDetalle = {},
            modifier = Modifier.width(170.dp),
        )
    }
}
