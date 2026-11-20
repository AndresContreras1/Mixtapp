package com.example.mixtapp.ui.screens.detail.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.EstadoDeReproduccion
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.BotonDeVistaPrevia
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.OpcionOvalada
import com.example.mixtapp.ui.utils.SectionTitle

// El bloque "Escuchar y ver" en una sola fila de tres pastillas del mismo tamano: el play para
// escuchar 30 segundos sin salir de la app y los enlaces con el logo de YouTube (solo canciones) y de
// Spotify. Debajo, si lo tiene, el Instagram del artista. LocalUriHandler abre el link en el navegador
// o en la app que lo tenga. Un link vacio no se muestra
@Composable
fun EnlacesDelItem(
    item: ItemUi,
    // null si este item no esta sonando
    estadoVistaPrevia: EstadoDeReproduccion?,
    @StringRes errorVistaPreviaRes: Int?,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SectionTitle(
            title = stringResource(R.string.escuchar_y_ver),
            actionText = null,
            onActionClick = null,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // El play: toda la pastilla se puede tocar, no solo el circulo
            Pastilla(
                texto = stringResource(
                    if (estadoVistaPrevia == EstadoDeReproduccion.Sonando) R.string.detener_corto else R.string.escuchar_corto
                ),
                onClick = onPlayClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag("btnVistaPrevia"),
            ) {
                BotonDeVistaPrevia(
                    estado = estadoVistaPrevia,
                    titulo = item.titulo,
                    tamano = 26.dp,
                    onClick = onPlayClick,
                )
            }

            if (item.youtubeUrl.isNotEmpty()) {
                Pastilla(
                    texto = stringResource(R.string.youtube),
                    onClick = { uriHandler.openUri(item.youtubeUrl) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btnYoutube"),
                ) {
                    // El texto de al lado ya dice a donde lleva
                    Image(
                        painter = painterResource(id = R.drawable.ic_youtube),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }

            if (item.spotifyUrl.isNotEmpty()) {
                Pastilla(
                    texto = stringResource(R.string.spotify),
                    onClick = { uriHandler.openUri(item.spotifyUrl) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btnSpotify"),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_spotify),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }

        ErrorMessage(messageRes = errorVistaPreviaRes)

        if (item.instagramUrl.isNotEmpty()) {
            OpcionOvalada(
                icono = Icons.Outlined.PhotoCamera,
                texto = stringResource(R.string.enlace_instagram_artista, item.instagram),
                onClick = { uriHandler.openUri(item.instagramUrl) },
                modifier = Modifier.testTag("btnInstagram"),
            )
        }
    }
}

// Una pastilla pequena de la fila: el icono o el logo a la izquierda y un texto corto
@Composable
private fun Pastilla(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icono: @Composable () -> Unit,
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.7f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            icono()

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = texto,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun EnlacesDelItemPreview() {
    MixtappTheme(darkTheme = true) {
        EnlacesDelItem(
            item = LocalPreviewProvider.emptinessMachine,
            estadoVistaPrevia = null,
            errorVistaPreviaRes = null,
            onPlayClick = {},
        )
    }
}
