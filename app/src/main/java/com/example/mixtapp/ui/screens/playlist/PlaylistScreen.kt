package com.example.mixtapp.ui.screens.playlist

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.EstadoDeReproduccion
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.PlaylistUi
import com.example.mixtapp.data.model.TipoDePlaylist
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.AppButton
import com.example.mixtapp.ui.utils.BackButton
import com.example.mixtapp.ui.utils.BotonDeVistaPrevia
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.ItemAsyncImage
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.MosaicoDePlaylist
import com.example.mixtapp.ui.utils.TonoDeFondo
import com.example.mixtapp.ui.utils.descripcionDePlaylist
import com.example.mixtapp.ui.utils.rebote
import com.example.mixtapp.ui.utils.texturaDeTarjeta
import com.example.mixtapp.ui.utils.tituloDePlaylist

@Composable
fun PlaylistScreen(
    playlistId: String,
    playlistViewModel: PlaylistViewModel,
    onItemClick: (tipo: String, itemId: String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by playlistViewModel.uiState.collectAsState()

    // Necesita el id de la playlist, que llega por la ruta
    LaunchedEffect(Unit) {
        playlistViewModel.abrirPlaylist(id = playlistId)
    }

    // Al salir, la musica deja de sonar
    DisposableEffect(Unit) {
        onDispose { playlistViewModel.detenerVistaPrevia() }
    }

    PlaylistScreenContent(
        isLoading = state.isLoading,
        playlist = state.playlist,
        vistaPreviaId = state.vistaPreviaId,
        vistaPreviaEstado = state.vistaPreviaEstado,
        reproduciendoTodo = state.reproduciendoTodo,
        errorMessageRes = state.errorMessageRes,
        errorVistaPreviaRes = state.errorVistaPreviaRes,
        onReproducirTodoClick = { playlistViewModel.alternarReproducirTodo() },
        onPlayClick = { playlistViewModel.alternarVistaPrevia(item = it) },
        onItemClick = { item -> onItemClick(item.tipo, item.id) },
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

// La playlist: el mosaico grande, su titulo, de donde sale, Reproducir todo y sus canciones con su
// play. Tocar una cancion abre su detalle
@Composable
fun PlaylistScreenContent(
    isLoading: Boolean,
    playlist: PlaylistUi?,
    // La cancion que suena y en que va; null si no suena nada
    vistaPreviaId: String?,
    vistaPreviaEstado: EstadoDeReproduccion?,
    reproduciendoTodo: Boolean,
    @StringRes errorMessageRes: Int?,
    @StringRes errorVistaPreviaRes: Int?,
    onReproducirTodoClick: () -> Unit,
    onPlayClick: (ItemUi) -> Unit,
    onItemClick: (ItemUi) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize().testTag("playlistScreen")) {
        AppBackground(tono = TonoDeFondo.Inicio)

        when {
            isLoading -> LoadingBox()
            playlist == null -> Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                BackButton(onBackClick = onBackClick)
                ErrorBox(messageRes = errorMessageRes ?: R.string.playlist_no_encontrada)
            }
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    val titulo = tituloDePlaylist(playlist)

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        BackButton(onBackClick = onBackClick, modifier = Modifier.align(Alignment.Start))

                        MosaicoDePlaylist(portadas = playlist.portadas, titulo = titulo, tamano = 220.dp)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = titulo,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = descripcionDePlaylist(playlist),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = stringResource(R.string.cantidad_canciones, playlist.canciones.size),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(top = 4.dp),
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        AppButton(
                            texto = stringResource(if (reproduciendoTodo) R.string.detener_todo else R.string.reproducir_todo),
                            onClick = onReproducirTodoClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btnReproducirTodo"),
                            cargando = false,
                        )

                        ErrorMessage(messageRes = errorVistaPreviaRes, modifier = Modifier.padding(top = 8.dp))
                    }
                }

                itemsIndexed(playlist.canciones, key = { _, cancion -> cancion.id }) { indice, cancion ->
                    CancionDePlaylist(
                        numero = indice + 1,
                        cancion = cancion,
                        estado = if (vistaPreviaId == cancion.id) vistaPreviaEstado else null,
                        onPlayClick = { onPlayClick(cancion) },
                        onClick = { onItemClick(cancion) },
                    )
                }
            }
        }
    }
}

// Una cancion de la playlist: su numero, la portada, el titulo y el artista, y su play. La que suena
// tiene el numero en rosado
@Composable
private fun CancionDePlaylist(
    numero: Int,
    cancion: ItemUi,
    // null si esta cancion no esta sonando
    estado: EstadoDeReproduccion?,
    onPlayClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .rebote()
            .texturaDeTarjeta(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(10.dp)
            .testTag("cancion_${cancion.id}"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = numero.toString(),
            color = if (estado != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.width(28.dp),
            textAlign = TextAlign.Center,
        )

        ItemAsyncImage(imagenUrl = cancion.imagenUrl, titulo = cancion.titulo, esquinas = 10.dp, modifier = Modifier.size(48.dp))

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = cancion.titulo,
                color = if (estado != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = cancion.artista,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        BotonDeVistaPrevia(estado = estado, titulo = cancion.titulo, tamano = 38.dp, onClick = onPlayClick)
    }
}

@Composable
@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
fun PlaylistScreenPreview() {
    MixtappTheme(darkTheme = true) {
        PlaylistScreenContent(
            isLoading = false,
            playlist = PlaylistUi(
                id = "mezcla_Nu metal",
                tipo = TipoDePlaylist.MezclaDeGenero,
                genero = "Nu metal",
                canciones = listOf(LocalPreviewProvider.emptinessMachine),
            ),
            vistaPreviaId = null,
            vistaPreviaEstado = null,
            reproduciendoTodo = false,
            errorMessageRes = null,
            errorVistaPreviaRes = null,
            onReproducirTodoClick = {},
            onPlayClick = {},
            onItemClick = {},
            onBackClick = {},
        )
    }
}
