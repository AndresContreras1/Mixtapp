package com.example.mixtapp.ui.screens.mapa

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.model.Coordenadas
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.ResenaEnMapaUi
import com.example.mixtapp.ui.screens.mapa.components.MapaOsm
import com.example.mixtapp.ui.screens.mapa.components.TarjetaDeResenaDelMapa
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.PedirPermisoDeUbicacion
import com.example.mixtapp.ui.utils.TonoDeFondo

@Composable
fun MapaScreen(
    mapaViewModel: MapaViewModel,
    onItemClick: (tipo: String, itemId: String) -> Unit,
    onAuthorClick: (String) -> Unit,
    onCommentsClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by mapaViewModel.uiState.collectAsState()

    // Con permiso de ubicacion el mapa se centra donde estoy; sin el, en Bogota
    PedirPermisoDeUbicacion(onConcedido = { mapaViewModel.centrarEnMiUbicacion() })

    MapaScreenContent(
        resenas = state.resenas,
        seleccionada = state.seleccionada,
        centro = state.centro,
        isLoading = state.isLoading,
        errorMessageRes = state.errorMessageRes,
        errorAccionRes = state.errorAccionRes,
        onMarcadorClick = { mapaViewModel.seleccionar(reviewId = it) },
        onCerrarClick = { mapaViewModel.cerrarSeleccion() },
        onLikeClick = { mapaViewModel.sendOrDeleteLike(reviewId = it) },
        onItemClick = { item -> onItemClick(item.tipo, item.id) },
        onAuthorClick = onAuthorClick,
        onCommentsClick = onCommentsClick,
        modifier = modifier,
    )
}

// El mapa ocupa toda la pantalla. Arriba va cuantas resenas hay en las ultimas 24 horas y, al
// tocar un marcador, abajo sale la tarjeta de su resena. Mientras llegan las resenas se ve la carga
// y si no cargaron, el error
@Composable
fun MapaScreenContent(
    resenas: List<ResenaEnMapaUi>,
    seleccionada: ResenaEnMapaUi?,
    centro: Coordenadas,
    isLoading: Boolean,
    @StringRes errorMessageRes: Int?,
    @StringRes errorAccionRes: Int?,
    onMarcadorClick: (String) -> Unit,
    onCerrarClick: () -> Unit,
    onLikeClick: (String) -> Unit,
    onItemClick: (ItemResumenUi) -> Unit,
    onAuthorClick: (String) -> Unit,
    onCommentsClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("mapaScreen")
    ) {
        AppBackground(tono = TonoDeFondo.Inicio)

        when {
            isLoading -> LoadingBox()
            errorMessageRes != null -> ErrorBox(messageRes = errorMessageRes)
            else -> {
                MapaOsm(
                    resenas = resenas,
                    centro = centro,
                    onMarcadorClick = onMarcadorClick,
                    modifier = Modifier.fillMaxSize(),
                )

                AvisoDelMapa(
                    cantidad = resenas.size,
                    modifier = Modifier.align(Alignment.TopCenter),
                )

                if (seleccionada != null) {
                    TarjetaDeResenaDelMapa(
                        resena = seleccionada,
                        errorAccionRes = errorAccionRes,
                        onCerrarClick = onCerrarClick,
                        onLikeClick = onLikeClick,
                        onItemClick = onItemClick,
                        onAuthorClick = onAuthorClick,
                        onCommentsClick = onCommentsClick,
                        modifier = Modifier.align(Alignment.BottomCenter),
                    )
                }
            }
        }
    }
}

// El aviso de arriba del mapa: cuantas resenas hay o que no hay ninguna
@Composable
private fun AvisoDelMapa(
    cantidad: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.92f))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.mapa_titulo),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = when (cantidad) {
                0 -> stringResource(R.string.mapa_sin_resenas)
                1 -> stringResource(R.string.mapa_una_resena)
                else -> stringResource(R.string.mapa_cantidad_resenas, cantidad)
            },
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.testTag("mapaCantidad"),
        )
    }
}

// El mapa de OpenStreetMap es una vista de Android y el preview no la dibuja: estos previews
// muestran el aviso y los estados que no tienen mapa
@Preview(showBackground = true)
@Composable
fun AvisoDelMapaPreview() {
    MixtappTheme(darkTheme = true) {
        AvisoDelMapa(cantidad = 3, modifier = Modifier.fillMaxWidth())
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun MapaScreenErrorPreview() {
    MixtappTheme(darkTheme = true) {
        MapaScreenContent(
            resenas = emptyList(),
            seleccionada = null,
            centro = CENTRO_POR_DEFECTO,
            isLoading = false,
            errorMessageRes = R.string.error_sin_conexion,
            errorAccionRes = null,
            onMarcadorClick = {},
            onCerrarClick = {},
            onLikeClick = {},
            onItemClick = {},
            onAuthorClick = {},
            onCommentsClick = {},
        )
    }
}
