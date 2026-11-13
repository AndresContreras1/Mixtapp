package com.example.mixtapp.ui.screens.resumen

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.toItemResumenUi
import com.example.mixtapp.ui.screens.resumen.components.TarjetaResumen
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.AppButton
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.ScreenTitle

@Composable
fun ResumenScreen(
    resumenViewModel: ResumenViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Mis reseñas, mis me gusta y mis seguidores llegan en vivo: la pantalla solo pinta el resumen
    val state by resumenViewModel.uiState.collectAsState()

    ResumenScreenContent(
        state = state,
        isLoading = state.isLoading,
        errorMessageRes = state.errorMessageRes,
        onSiguienteClick = { resumenViewModel.mostrarSiguiente() },
        onVerDeNuevoClick = { resumenViewModel.verDeNuevo() },
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
fun ResumenScreenContent(
    state: ResumenState,
    isLoading: Boolean,
    @StringRes errorMessageRes: Int?,
    onSiguienteClick: () -> Unit,
    onVerDeNuevoClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("resumenScreen")
    ) {
        AppBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            ScreenTitle(
                title = stringResource(R.string.resumen_titulo),
                onBackClick = onBackClick,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )

            when {
                isLoading -> LoadingBox()
                errorMessageRes != null -> ErrorBox(messageRes = errorMessageRes)
                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.resumen_subtitulo),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }

                    // Cada tarjeta aparece con una animacion cuando se descubre
                    itemsIndexed(state.tarjetas) { indice, tarjeta ->
                        AnimatedVisibility(
                            visible = indice < state.reveladas,
                            enter = fadeIn(),
                            exit = fadeOut(),
                        ) {
                            ContenidoDeTarjeta(
                                tarjeta = tarjeta,
                                state = state,
                                modifier = Modifier.testTag("tarjeta_$tarjeta"),
                            )
                        }
                    }

                    item {
                        val quedanPorVer = state.reveladas < state.tarjetas.size

                        AppButton(
                            texto = stringResource(if (quedanPorVer) R.string.resumen_siguiente else R.string.resumen_ver_de_nuevo),
                            onClick = if (quedanPorVer) onSiguienteClick else onVerDeNuevoClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btnSiguienteResumen"),
                        )
                    }
                }
            }
        }
    }
}

// Que dice cada tarjeta, segun su tipo
@Composable
private fun ContenidoDeTarjeta(
    tarjeta: String,
    state: ResumenState,
    modifier: Modifier = Modifier,
) {
    when (tarjeta) {
        TARJETA_RESENAS -> TarjetaResumen(
            titulo = stringResource(R.string.resumen_resenas_titulo),
            valor = state.totalResenas.toString(),
            detalle = if (state.totalResenas == 0) {
                stringResource(R.string.resumen_sin_resenas)
            } else {
                stringResource(R.string.resumen_promedio, state.promedio.toString())
            },
            modifier = modifier,
        )
        TARJETA_GENERO -> TarjetaResumen(
            titulo = stringResource(R.string.resumen_genero_titulo),
            valor = state.generoFavorito ?: "",
            detalle = stringResource(R.string.resumen_genero_detalle),
            modifier = modifier,
        )
        TARJETA_ARTISTA -> TarjetaResumen(
            titulo = stringResource(R.string.resumen_artista_titulo),
            valor = state.artistaFavorito ?: "",
            detalle = stringResource(R.string.resumen_artista_detalle),
            modifier = modifier,
        )
        TARJETA_MEJOR -> TarjetaResumen(
            titulo = stringResource(R.string.resumen_mejor_titulo),
            valor = state.mejorCalificado?.titulo ?: "",
            detalle = stringResource(R.string.resumen_mejor_detalle, state.mejorCalificacion),
            imagenUrl = state.mejorCalificado?.imagenUrl,
            modifier = modifier,
        )
        TARJETA_QUERIDA -> TarjetaResumen(
            titulo = stringResource(R.string.resumen_querida_titulo),
            valor = state.masQuerida?.item?.titulo ?: "",
            detalle = stringResource(R.string.resumen_querida_detalle, state.masQuerida?.likes ?: 0),
            imagenUrl = state.masQuerida?.item?.imagenUrl,
            modifier = modifier,
        )
        // TARJETA_SOCIAL
        else -> TarjetaResumen(
            titulo = stringResource(R.string.resumen_social_titulo),
            valor = stringResource(R.string.resumen_me_gusta, state.totalMeGusta),
            detalle = stringResource(R.string.resumen_social_detalle, state.seguidores, state.seguidos),
            modifier = modifier,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ResumenScreenPreview() {
    MixtappTheme(darkTheme = true) {
        ResumenScreenContent(
            state = ResumenState(
                totalResenas = 12,
                promedio = 4.2,
                totalMeGusta = 30,
                generoFavorito = "Nu metal",
                artistaFavorito = "Linkin Park",
                mejorCalificado = LocalPreviewProvider.fromZero.toItemResumenUi(),
                mejorCalificacion = 5,
                seguidores = 8,
                seguidos = 5,
                tarjetas = listOf(TARJETA_RESENAS, TARJETA_GENERO, TARJETA_ARTISTA, TARJETA_MEJOR, TARJETA_SOCIAL),
                reveladas = 3,
                isLoading = false,
            ),
            isLoading = false,
            errorMessageRes = null,
            onSiguienteClick = {},
            onVerDeNuevoClick = {},
            onBackClick = {},
        )
    }
}
