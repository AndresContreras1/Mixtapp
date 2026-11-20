package com.example.mixtapp.ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.PlaylistUi
import com.example.mixtapp.data.model.TipoDePlaylist
import com.example.mixtapp.ui.theme.MixtappTheme

// El titulo de una playlist segun su tipo ("Tu mezcla de Metal", "Top Colombia")
@Composable
fun tituloDePlaylist(playlist: PlaylistUi): String = when (playlist.tipo) {
    TipoDePlaylist.MezclaDeGenero -> stringResource(R.string.playlist_mezcla, playlist.genero ?: "")
    TipoDePlaylist.Descubre -> stringResource(R.string.playlist_descubre)
    TipoDePlaylist.TopColombia -> stringResource(R.string.playlist_top_colombia)
    TipoDePlaylist.LoMasEscuchado -> stringResource(R.string.playlist_lo_mas_escuchado)
}

// La linea que explica de donde sale la playlist
@Composable
fun descripcionDePlaylist(playlist: PlaylistUi): String = when (playlist.tipo) {
    TipoDePlaylist.MezclaDeGenero -> stringResource(R.string.playlist_mezcla_descripcion, playlist.genero ?: "")
    TipoDePlaylist.Descubre -> stringResource(R.string.playlist_descubre_descripcion)
    TipoDePlaylist.TopColombia -> stringResource(R.string.playlist_top_colombia_descripcion)
    TipoDePlaylist.LoMasEscuchado -> stringResource(R.string.playlist_lo_mas_escuchado_descripcion)
}

// La portada de una playlist: el mosaico de las portadas de sus primeras cuatro canciones. Con menos
// de cuatro, la primera ocupa todo
@Composable
fun MosaicoDePlaylist(
    portadas: List<String>,
    titulo: String,
    tamano: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(tamano)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp)),
    ) {
        if (portadas.size < 4) {
            ItemAsyncImage(imagenUrl = portadas.firstOrNull() ?: "", titulo = titulo, esquinas = 0.dp, modifier = Modifier.size(tamano))
        } else {
            Column {
                portadas.chunked(2).forEach { fila ->
                    Row {
                        fila.forEach { portada ->
                            ItemAsyncImage(imagenUrl = portada, titulo = titulo, esquinas = 0.dp, modifier = Modifier.size(tamano / 2))
                        }
                    }
                }
            }
        }
    }
}

// Una playlist en la fila del inicio: el mosaico, el titulo y cuantas canciones tiene. Tocarla abre
// su pantalla
@Composable
fun TarjetaDePlaylist(
    playlist: PlaylistUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tamano: Dp = 150.dp,
) {
    val titulo = tituloDePlaylist(playlist)

    Column(
        modifier = modifier
            .width(tamano)
            .rebote()
            .clickable { onClick() }
            .testTag("playlist_${playlist.id}"),
    ) {
        MosaicoDePlaylist(portadas = playlist.portadas, titulo = titulo, tamano = tamano)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = titulo,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = stringResource(R.string.cantidad_canciones, playlist.canciones.size),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun TarjetaDePlaylistPreview() {
    MixtappTheme(darkTheme = true) {
        TarjetaDePlaylist(
            playlist = PlaylistUi(
                id = "mezcla_Nu metal",
                tipo = TipoDePlaylist.MezclaDeGenero,
                genero = "Nu metal",
                canciones = listOf(LocalPreviewProvider.emptinessMachine, LocalPreviewProvider.fromZero),
            ),
            onClick = {},
        )
    }
}
