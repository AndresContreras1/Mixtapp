package com.example.mixtapp.ui.screens.mapa

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.model.Coordenadas
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.ResenaEnMapaUi
import com.example.mixtapp.ui.screens.mapa.components.MapaOsm
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.PedirPermisoDeUbicacion
import com.example.mixtapp.ui.utils.ReviewCard

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
// tocar un marcador, abajo sale la tarjeta de su resena
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
        MapaOsm(
            resenas = resenas,
            centro = centro,
            onMarcadorClick = onMarcadorClick,
            modifier = Modifier.fillMaxSize(),
        )

        // El aviso de arriba: cuantas hay, que no hay ninguna o por que no cargaron
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
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
                text = when {
                    isLoading -> stringResource(R.string.cargando_mapa)
                    resenas.isEmpty() -> stringResource(R.string.mapa_sin_resenas)
                    else -> stringResource(R.string.mapa_cantidad_resenas, resenas.size)
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.testTag("mapaCantidad"),
            )
            ErrorMessage(messageRes = errorMessageRes)
        }

        if (seleccionada != null) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("tarjetaMapa"),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    IconButton(
                        onClick = onCerrarClick,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.surfaceContainer),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.cerrar_resena_del_mapa),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }

                ErrorMessage(messageRes = errorAccionRes)

                ReviewCard(
                    review = seleccionada.review,
                    onAuthorClick = onAuthorClick,
                    onLikeClick = onLikeClick,
                    onCommentsClick = onCommentsClick,
                    mostrarItem = true,
                    onItemClick = onItemClick,
                )
            }
        }
    }
}
