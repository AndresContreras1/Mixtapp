package com.example.mixtapp.ui.screens.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.EstadoDeReproduccion
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.ui.screens.home.components.HomeHeader
import com.example.mixtapp.ui.screens.home.components.HomeItemRow
import com.example.mixtapp.ui.screens.home.components.ResenasDeSeguidos
import com.example.mixtapp.ui.screens.home.components.TrendingCard
import com.example.mixtapp.ui.screens.home.model.homeFilters
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.BarraSuperior
import com.example.mixtapp.ui.utils.EmptyMessage
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.EspacioDelFab
import com.example.mixtapp.ui.utils.FilaDeOpciones
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.OpcionUi
import com.example.mixtapp.ui.utils.SectionTitle
import com.example.mixtapp.ui.utils.TonoDeFondo

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onItemClick: (String, String) -> Unit,
    onAuthorClick: (String) -> Unit,
    onCommentsClick: (String) -> Unit,
    onFollowingClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onDescubrirPersonasClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // El catalogo, mis favoritos y mis seguidos llegan en vivo desde el ViewModel: la pantalla
    // solo pinta lo que ya viene armado
    val state by homeViewModel.uiState.collectAsState()

    // Cada vez que se vuelve al inicio (del detalle, de otra pestana) sale otro album en tendencia.
    // Los flujos no se piden aqui: ya se escuchan desde el init del ViewModel
    LaunchedEffect(Unit) {
        homeViewModel.elegirAlbumEnTendencia()
    }

    // Al salir del inicio el fragmento de la tarjeta deja de sonar
    DisposableEffect(Unit) {
        onDispose { homeViewModel.detenerVistaPrevia() }
    }

    HomeScreenContent(
        isLoading = state.isLoading,
        errorMessageRes = state.errorMessageRes,
        filters = state.filters,
        selectedFilterId = state.selectedFilterId,
        enTendencia = state.enTendencia,
        enTendenciaColombia = state.enTendenciaColombia,
        estadoVistaPrevia = state.vistaPreviaEstado,
        errorVistaPreviaRes = state.errorVistaPreviaRes,
        canciones = state.canciones,
        albumes = state.albumes,
        artistas = state.artistas,
        resenasDeSeguidos = state.resenasDeSeguidos,
        errorListasRes = state.errorFavoritosRes ?: state.errorSeguidosRes,
        errorAccionRes = state.errorAccionRes,
        onFilterSelected = { homeViewModel.updateSelectedFilter(filtroId = it) },
        onItemClick = onItemClick,
        onPlayClick = { homeViewModel.alternarVistaPrevia(item = it) },
        onAuthorClick = onAuthorClick,
        onLikeClick = { homeViewModel.sendOrDeleteLike(reviewId = it) },
        onCommentsClick = onCommentsClick,
        onFollowingClick = onFollowingClick,
        onNotificationsClick = onNotificationsClick,
        onDescubrirPersonasClick = onDescubrirPersonasClick,
        modifier = modifier,
    )
}

@Composable
fun HomeScreenContent(
    isLoading: Boolean,
    @StringRes errorMessageRes: Int?,
    filters: List<OpcionUi>,
    selectedFilterId: String,
    enTendencia: ItemUi?,
    enTendenciaColombia: Boolean,
    // null si la tarjeta en tendencia no esta sonando
    estadoVistaPrevia: EstadoDeReproduccion?,
    @StringRes errorVistaPreviaRes: Int?,
    canciones: List<ItemUi>,
    albumes: List<ItemUi>,
    artistas: List<ItemUi>,
    resenasDeSeguidos: List<ReviewUi>,
    @StringRes errorListasRes: Int?,
    @StringRes errorAccionRes: Int?,
    onFilterSelected: (String) -> Unit,
    onItemClick: (String, String) -> Unit,
    onPlayClick: (ItemUi) -> Unit,
    onAuthorClick: (String) -> Unit,
    onLikeClick: (String) -> Unit,
    onCommentsClick: (String) -> Unit,
    onFollowingClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onDescubrirPersonasClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("homeScreen")
    ) {
        AppBackground(tono = TonoDeFondo.Inicio)

        Column(modifier = Modifier.fillMaxSize()) {
            // El logo y los accesos quedan fijos arriba, tambien mientras carga
            BarraSuperior {
                HomeHeader(
                    onFollowingClick = onFollowingClick,
                    onNotificationsClick = onNotificationsClick,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when {
                    isLoading -> LoadingBox()
                    // Sin catalogo no hay nada que mostrar: solo el motivo del error
                    errorMessageRes != null -> ErrorBox(messageRes = errorMessageRes)
                    // El orden cuenta lo que es Mixtapp: lo que esta sonando, lo que opinan las
                    // personas que sigo y despues el catalogo para descubrir
                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = EspacioDelFab),
                        verticalArrangement = Arrangement.spacedBy(28.dp),
                    ) {
                        item {
                            Column {
                                FilaDeOpciones(
                                    opciones = filters,
                                    elegidaId = selectedFilterId,
                                    onOpcionClick = onFilterSelected,
                                    prefijoTestTag = "filtro",
                                )

                                // Si fallan mis favoritos o mis seguidos, el catalogo se sigue viendo
                                ErrorMessage(messageRes = errorListasRes, modifier = Modifier.padding(top = 8.dp))
                            }
                        }

                        // Solo en Para ti y en Tendencias: el ViewModel la deja en null en los demas
                        if (enTendencia != null) {
                            item {
                                Column {
                                    SectionTitle(
                                        title = stringResource(R.string.now_trending),
                                        actionText = null,
                                        onActionClick = null,
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    TrendingCard(
                                        item = enTendencia,
                                        enColombia = enTendenciaColombia,
                                        estadoVistaPrevia = estadoVistaPrevia,
                                        onClick = { onItemClick(enTendencia.tipo, enTendencia.id) },
                                        onPlayClick = { onPlayClick(enTendencia) },
                                        modifier = Modifier.testTag("tarjetaTendencia"),
                                    )

                                    ErrorMessage(
                                        messageRes = errorVistaPreviaRes,
                                        modifier = Modifier.padding(top = 8.dp),
                                    )
                                }
                            }
                        }

                        item {
                            ResenasDeSeguidos(
                                resenas = resenasDeSeguidos,
                                errorAccionRes = errorAccionRes,
                                onAuthorClick = onAuthorClick,
                                onLikeClick = onLikeClick,
                                onCommentsClick = onCommentsClick,
                                onItemClick = { item -> onItemClick(item.tipo, item.id) },
                                onDescubrirPersonasClick = onDescubrirPersonasClick,
                            )
                        }

                        // Una fila vacia no se muestra; si las tres lo estan, el filtro no tiene nada todavia
                        if (canciones.isEmpty() && albumes.isEmpty() && artistas.isEmpty()) {
                            item {
                                EmptyMessage(messageRes = R.string.sin_items_filtro, modifier = Modifier.fillMaxWidth())
                            }
                        }

                        if (canciones.isNotEmpty()) {
                            item {
                                HomeItemRow(
                                    titulo = stringResource(R.string.canciones_populares),
                                    items = canciones,
                                    onItemClick = { onItemClick(it.tipo, it.id) },
                                )
                            }
                        }

                        if (albumes.isNotEmpty()) {
                            item {
                                HomeItemRow(
                                    titulo = stringResource(R.string.albumes_populares),
                                    items = albumes,
                                    onItemClick = { onItemClick(it.tipo, it.id) },
                                )
                            }
                        }

                        if (artistas.isNotEmpty()) {
                            item {
                                HomeItemRow(
                                    titulo = stringResource(R.string.artistas_populares),
                                    items = artistas,
                                    onItemClick = { onItemClick(it.tipo, it.id) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
fun HomeScreenPreview() {
    MixtappTheme(darkTheme = true) {
        HomeScreenContent(
            isLoading = false,
            errorMessageRes = null,
            filters = homeFilters,
            selectedFilterId = homeFilters.first().id,
            enTendencia = LocalPreviewProvider.karolG,
            enTendenciaColombia = false,
            estadoVistaPrevia = null,
            errorVistaPreviaRes = null,
            canciones = listOf(LocalPreviewProvider.emptinessMachine),
            albumes = listOf(LocalPreviewProvider.karolG, LocalPreviewProvider.fromZero),
            artistas = listOf(LocalPreviewProvider.mikeShinoda),
            resenasDeSeguidos = listOf(LocalPreviewProvider.review),
            errorListasRes = null,
            errorAccionRes = null,
            onFilterSelected = {},
            onItemClick = { _, _ -> },
            onPlayClick = {},
            onAuthorClick = {},
            onLikeClick = {},
            onCommentsClick = {},
            onFollowingClick = {},
            onNotificationsClick = {},
            onDescubrirPersonasClick = {},
        )
    }
}
