package com.example.mixtapp.ui.screens.detail.components

import androidx.annotation.DrawableRes
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
import com.example.mixtapp.ui.utils.TarjetaTranslucida

// El bloque "Escuchar y ver": primero el fragmento de 30 segundos que suena sin salir de la app y
// debajo los enlaces para seguir afuera, cada uno con el logo de su app (YouTube solo en canciones,
// Spotify y el Instagram del artista). LocalUriHandler abre el link en el navegador o en la app que lo
// tenga. Un link vacio no se muestra
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

        // El fragmento: toda la tarjeta se puede tocar, no solo el boton
        TarjetaTranslucida(
            onClick = onPlayClick,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("btnVistaPrevia"),
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BotonDeVistaPrevia(
                    estado = estadoVistaPrevia,
                    titulo = item.titulo,
                    tamano = 52.dp,
                    onClick = onPlayClick,
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = stringResource(
                            when (estadoVistaPrevia) {
                                EstadoDeReproduccion.Cargando -> R.string.cargando_fragmento
                                EstadoDeReproduccion.Sonando -> R.string.detener_fragmento
                                else -> R.string.escuchar_fragmento
                            }
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = item.titulo,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                    )
                }
            }
        }

        ErrorMessage(messageRes = errorVistaPreviaRes)

        // Para seguir afuera: los dos enlaces lado a lado, cada uno con su logo
        if (item.youtubeUrl.isNotEmpty() || item.spotifyUrl.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (item.youtubeUrl.isNotEmpty()) {
                    EnlaceConLogo(
                        logo = R.drawable.ic_youtube,
                        texto = stringResource(R.string.youtube),
                        onClick = { uriHandler.openUri(item.youtubeUrl) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btnYoutube"),
                    )
                }

                if (item.spotifyUrl.isNotEmpty()) {
                    EnlaceConLogo(
                        logo = R.drawable.ic_spotify,
                        texto = stringResource(R.string.spotify),
                        onClick = { uriHandler.openUri(item.spotifyUrl) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btnSpotify"),
                    )
                }
            }
        }

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

// Un enlace ovalado con el logo de la app a la que lleva y su nombre. Los logos van con sus colores:
// asi se reconocen sin leer
@Composable
private fun EnlaceConLogo(
    @DrawableRes logo: Int,
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            // El texto de al lado ya dice a donde lleva
            Image(
                painter = painterResource(id = logo),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = texto,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleSmall,
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
