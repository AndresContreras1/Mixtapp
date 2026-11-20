package com.example.mixtapp.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.EstadoDeReproduccion
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.ui.theme.Black
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.theme.White
import com.example.mixtapp.ui.utils.BotonDeVistaPrevia
import com.example.mixtapp.ui.utils.ItemAsyncImage
import com.example.mixtapp.ui.utils.rebote

// La tarjeta grande del inicio: la portada del album en tendencia a todo el ancho con un degradado
// suave abajo (sin franja), la etiqueta de en que es tendencia, su titulo, el artista, su puesto y el
// play para escuchar 30 segundos sin salir del inicio
@Composable
fun TrendingCard(
    item: ItemUi,
    enColombia: Boolean,
    // null si este album no esta sonando
    estadoVistaPrevia: EstadoDeReproduccion?,
    onClick: () -> Unit,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .rebote()
            .fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            ItemAsyncImage(
                imagenUrl = item.imagenUrl,
                titulo = item.titulo,
                esquinas = 28.dp,
                modifier = Modifier.fillMaxSize(),
            )

            // El degradado va de transparente a oscuro: el texto se lee sin tapar la portada
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.35f to Color.Transparent,
                            1f to Black.copy(alpha = 0.8f),
                        )
                    )
            )

            EtiquetaDeTendencia(
                texto = if (enColombia) {
                    stringResource(R.string.tendencia_en_colombia)
                } else {
                    stringResource(R.string.tendencia_en, item.genero)
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(14.dp),
            )

            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 14.dp, bottom = 14.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.titulo,
                        color = White,
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = item.subtitulo,
                        color = White.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    // Si ningun album esta en la lista, la tarjeta muestra el mas popular sin puesto
                    val puesto = if (enColombia) item.tendenciaColombia else item.tendencia
                    if (puesto != null) {
                        Text(
                            text = if (enColombia) {
                                stringResource(R.string.puesto_en_colombia, puesto)
                            } else {
                                stringResource(R.string.puesto_en_genero, puesto, item.genero)
                            },
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                BotonDeVistaPrevia(
                    estado = estadoVistaPrevia,
                    titulo = item.titulo,
                    tamano = 52.dp,
                    onClick = onPlayClick,
                )
            }
        }
    }
}

// La pastilla oscura de arriba con la flecha de tendencia
@Composable
private fun EtiquetaDeTendencia(
    texto: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(Black.copy(alpha = 0.45f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
            contentDescription = null,
            tint = White,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = texto,
            color = White,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun TrendingCardPreview() {
    MixtappTheme(darkTheme = true) {
        TrendingCard(
            item = LocalPreviewProvider.karolG,
            enColombia = false,
            estadoVistaPrevia = null,
            onClick = {},
            onPlayClick = {},
        )
    }
}
