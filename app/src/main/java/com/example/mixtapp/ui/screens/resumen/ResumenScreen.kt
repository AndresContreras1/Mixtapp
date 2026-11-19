package com.example.mixtapp.ui.screens.resumen

import android.content.Intent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.MicExternalOn
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.toItemResumenUi
import com.example.mixtapp.ui.screens.resumen.components.TarjetaResumen
import com.example.mixtapp.ui.theme.Chicle
import com.example.mixtapp.ui.theme.Coral
import com.example.mixtapp.ui.theme.Crimson300
import com.example.mixtapp.ui.theme.Magenta
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.theme.Violeta
import com.example.mixtapp.ui.theme.White
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.AppButton
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.ScreenTitle
import com.example.mixtapp.ui.utils.TonoDeFondo

@Composable
fun ResumenScreen(
    resumenViewModel: ResumenViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Mis reseñas, mis me gusta y mis seguidores llegan en vivo: la pantalla solo pinta el resumen
    val state by resumenViewModel.uiState.collectAsState()
    val context = LocalContext.current

    ResumenScreenContent(
        state = state,
        onBotonClick = { resumenViewModel.avanzar() },
        // Compartir abre la lista de apps del celular (WhatsApp, Instagram...) con el texto listo
        onCompartirClick = { texto ->
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, texto)
            }
            context.startActivity(Intent.createChooser(intent, null))
        },
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

// La version sin ViewModel, para el preview. Tu año se ve como una historia: una tarjeta a la vez,
// con los segmentos de arriba que cuentan cuantas van, y se avanza tocando la tarjeta o el boton
@Composable
fun ResumenScreenContent(
    state: ResumenState,
    onBotonClick: () -> Unit,
    onCompartirClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("resumenScreen")
    ) {
        AppBackground(tono = TonoDeFondo.Resumen)

        Column(modifier = Modifier.fillMaxSize()) {
            ScreenTitle(
                title = stringResource(R.string.resumen_titulo_anio, state.anio),
                onBackClick = onBackClick,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )

            when {
                state.isLoading -> LoadingBox()
                state.errorMessageRes != null -> ErrorBox(messageRes = state.errorMessageRes)
                else -> Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                ) {
                    SegmentosDeLaHistoria(total = state.tarjetas.size, vistas = state.reveladas)

                    ErrorMessage(messageRes = state.errorDatosRes)

                    // La tarjeta de ahora; al cambiar se desvanece hacia la siguiente
                    val actual = state.tarjetas.getOrNull(state.reveladas - 1)
                    Crossfade(
                        targetState = actual,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(vertical = 14.dp)
                            .clickable { onBotonClick() },
                        label = "tarjetaDelResumen",
                    ) { tarjeta ->
                        if (tarjeta != null) {
                            ContenidoDeTarjeta(
                                tarjeta = tarjeta,
                                state = state,
                                modifier = Modifier.testTag("tarjeta_$tarjeta"),
                            )
                        }
                    }

                    // En la ultima tarjeta, la invitacion a compartir el resumen
                    if (!state.quedanPorVer) {
                        val texto = stringResource(
                            R.string.resumen_compartir_texto,
                            state.anio,
                            state.totalResenas,
                            state.totalMeGusta,
                        )

                        OutlinedButton(
                            onClick = { onCompartirClick(texto) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .padding(bottom = 8.dp)
                                .testTag("btnCompartirResumen"),
                        ) {
                            Text(
                                text = stringResource(R.string.resumen_compartir),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }

                    AppButton(
                        texto = stringResource(if (state.quedanPorVer) R.string.resumen_siguiente else R.string.resumen_ver_de_nuevo),
                        onClick = onBotonClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btnSiguienteResumen"),
                        cargando = false,
                    )

                    Text(
                        text = stringResource(R.string.resumen_toca),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    )
                }
            }
        }
    }
}

// Los segmentos de arriba, como en una historia: los que ya se vieron van llenos
@Composable
private fun SegmentosDeLaHistoria(
    total: Int,
    vistas: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        repeat(total) { indice ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(if (indice < vistas) White else White.copy(alpha = 0.25f))
            )
        }
    }
}

// Que dice cada tarjeta, segun su tipo. Cada una tiene su icono y su degradado. Con pocos datos, el
// genero y el artista no se afirman: la linea de abajo lo dice
@Composable
private fun ContenidoDeTarjeta(
    tarjeta: String,
    state: ResumenState,
    modifier: Modifier = Modifier,
) {
    val detalleDeGustos = if (state.pocosDatos) stringResource(R.string.resumen_pocos_datos) else null

    when (tarjeta) {
        TARJETA_RESENAS -> TarjetaResumen(
            titulo = stringResource(R.string.resumen_resenas_titulo),
            valor = state.totalResenas.toString(),
            detalle = if (state.totalResenas == 0) {
                stringResource(R.string.resumen_sin_resenas)
            } else {
                stringResource(R.string.resumen_promedio, state.promedio.toString())
            },
            icono = Icons.Outlined.RateReview,
            colores = listOf(Coral, Magenta),
            modifier = modifier,
        )
        TARJETA_GENERO -> TarjetaResumen(
            titulo = stringResource(R.string.resumen_genero_titulo),
            valor = state.generoFavorito ?: "",
            detalle = detalleDeGustos ?: stringResource(R.string.resumen_genero_detalle),
            icono = Icons.Outlined.LibraryMusic,
            colores = listOf(Violeta, Magenta),
            modifier = modifier,
        )
        TARJETA_ARTISTA -> TarjetaResumen(
            titulo = stringResource(R.string.resumen_artista_titulo),
            valor = state.artistaFavorito ?: "",
            detalle = detalleDeGustos ?: stringResource(R.string.resumen_artista_detalle),
            icono = Icons.Outlined.MicExternalOn,
            colores = listOf(Magenta, Crimson300),
            modifier = modifier,
        )
        TARJETA_MEJOR -> TarjetaResumen(
            titulo = stringResource(R.string.resumen_mejor_titulo),
            valor = state.mejorCalificado?.titulo ?: "",
            detalle = stringResource(
                R.string.resumen_mejor_detalle,
                state.mejorCalificado?.subtitulo ?: "",
                state.mejorCalificacion,
            ),
            icono = Icons.Outlined.Album,
            colores = listOf(Crimson300, Coral),
            imagenUrl = state.mejorCalificado?.imagenUrl,
            modifier = modifier,
        )
        TARJETA_QUERIDA -> TarjetaResumen(
            titulo = stringResource(R.string.resumen_querida_titulo),
            valor = state.masQuerida?.item?.titulo ?: "",
            detalle = stringResource(R.string.resumen_querida_detalle, state.masQuerida?.likes ?: 0),
            icono = Icons.Outlined.Favorite,
            colores = listOf(Chicle, Violeta),
            imagenUrl = state.masQuerida?.item?.imagenUrl,
            modifier = modifier,
        )
        // TARJETA_SOCIAL
        else -> TarjetaResumen(
            titulo = stringResource(R.string.resumen_social_titulo),
            valor = state.totalMeGusta.toString(),
            detalle = stringResource(R.string.resumen_social_detalle, state.seguidores, state.seguidos),
            icono = Icons.Outlined.Groups,
            colores = listOf(Violeta, Coral),
            modifier = modifier,
        )
    }
}

@Composable
@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
fun ResumenScreenPreview() {
    MixtappTheme(darkTheme = true) {
        ResumenScreenContent(
            state = ResumenState(
                anio = 2026,
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
                reveladas = 1,
                isLoading = false,
            ),
            onBotonClick = {},
            onCompartirClick = {},
            onBackClick = {},
        )
    }
}
