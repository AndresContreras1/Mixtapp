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
import androidx.compose.foundation.lazy.items
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
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.ui.screens.home.components.HomeFilters
import com.example.mixtapp.ui.screens.home.components.HomeHeader
import com.example.mixtapp.ui.screens.home.components.HomeItemRow
import com.example.mixtapp.ui.screens.home.components.TrendingCard
import com.example.mixtapp.ui.screens.home.model.HomeFilterUi
import com.example.mixtapp.ui.screens.home.model.homeFilters
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.EmptyMessage
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.ReviewCard
import com.example.mixtapp.ui.utils.SectionTitle

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onItemClick: (String, String) -> Unit,
    onAuthorClick: (String) -> Unit,
    onCommentsClick: (String) -> Unit,
    onFollowingClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // El catalogo, mis favoritos y mis seguidos llegan en vivo desde el ViewModel: la pantalla
    // solo pinta lo que ya viene armado
    val state by homeViewModel.uiState.collectAsState()

    HomeScreenContent(
        isLoading = state.isLoading,
        errorMessageRes = state.errorMessageRes,
        filters = state.filters,
        selectedFilterId = state.selectedFilterId,
        enTendencia = state.enTendencia,
        canciones = state.canciones,
        albumes = state.albumes,
        artistas = state.artistas,
        resenasDeSeguidos = state.resenasDeSeguidos,
        errorListasRes = state.errorFavoritosRes ?: state.errorSeguidosRes,
        errorAccionRes = state.errorAccionRes,
        onFilterSelected = { homeViewModel.updateSelectedFilter(filtroId = it) },
        onItemClick = onItemClick,
        onAuthorClick = onAuthorClick,
        onLikeClick = { homeViewModel.sendOrDeleteLike(reviewId = it) },
        onCommentsClick = onCommentsClick,
        onFollowingClick = onFollowingClick,
        onNotificationsClick = onNotificationsClick,
        modifier = modifier,
    )
}

@Composable
fun HomeScreenContent(
    isLoading: Boolean,
    @StringRes errorMessageRes: Int?,
    filters: List<HomeFilterUi>,
    selectedFilterId: String,
    enTendencia: ItemUi?,
    canciones: List<ItemUi>,
    albumes: List<ItemUi>,
    artistas: List<ItemUi>,
    resenasDeSeguidos: List<ReviewUi>,
    @StringRes errorListasRes: Int?,
    @StringRes errorAccionRes: Int?,
    onFilterSelected: (String) -> Unit,
    onItemClick: (String, String) -> Unit,
    onAuthorClick: (String) -> Unit,
    onLikeClick: (String) -> Unit,
    onCommentsClick: (String) -> Unit,
    onFollowingClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("homeScreen")
    ) {
        AppBackground()

        when {
            isLoading -> LoadingBox()
            // Sin catalogo no hay nada que mostrar: solo el motivo del error
            errorMessageRes != null -> ErrorBox(messageRes = errorMessageRes)
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                item {
                    HomeHeader(
                        onFollowingClick = onFollowingClick,
                        onNotificationsClick = onNotificationsClick,
                    )
                }

                item {
                    Column {
                        HomeFilters(
                            filters = filters,
                            selectedId = selectedFilterId,
                            onFilterSelected = onFilterSelected,
                        )

                        // Si fallan mis favoritos o mis seguidos, el catalogo se sigue viendo
                        ErrorMessage(messageRes = errorListasRes, modifier = Modifier.padding(top = 8.dp))
                    }
                }

                if (enTendencia != null) {
                    item {
                        Column {
                            SectionTitle(title = stringResource(R.string.now_trending))

                            Spacer(modifier = Modifier.height(8.dp))

                            TrendingCard(
                                item = enTendencia,
                                onClick = { onItemClick(enTendencia.tipo, enTendencia.id) },
                                modifier = Modifier.testTag("tarjetaTendencia"),
                            )
                        }
                    }
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
                            titulo = stringResource(R.string.popular_albums),
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

                item {
                    Column {
                        SectionTitle(title = stringResource(R.string.resenas_de_seguidos))

                        // El error del like se ve junto a las tarjetas donde se toco
                        ErrorMessage(messageRes = errorAccionRes)

                        if (resenasDeSeguidos.isEmpty()) {
                            EmptyMessage(messageRes = R.string.sin_actividad_seguidos, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }

                items(resenasDeSeguidos) { review ->
                    ReviewCard(
                        review = review,
                        onAuthorClick = onAuthorClick,
                        onLikeClick = onLikeClick,
                        onCommentsClick = onCommentsClick,
                        mostrarItem = true,
                        onItemClick = { item -> onItemClick(item.tipo, item.id) },
                    )
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
            canciones = listOf(LocalPreviewProvider.emptinessMachine),
            albumes = listOf(LocalPreviewProvider.karolG, LocalPreviewProvider.fromZero),
            artistas = listOf(LocalPreviewProvider.mikeShinoda),
            resenasDeSeguidos = listOf(LocalPreviewProvider.review),
            errorListasRes = null,
            errorAccionRes = null,
            onFilterSelected = {},
            onItemClick = { _, _ -> },
            onAuthorClick = {},
            onLikeClick = {},
            onCommentsClick = {},
            onFollowingClick = {},
            onNotificationsClick = {},
        )
    }
}
