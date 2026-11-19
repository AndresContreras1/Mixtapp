package com.example.mixtapp.ui.screens.search

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
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
import com.example.mixtapp.data.local.LocalSearchCategoriesProvider
import com.example.mixtapp.data.model.ItemConResenasUi
import com.example.mixtapp.data.model.SearchCategoryUi
import com.example.mixtapp.data.model.UsuarioUi
import com.example.mixtapp.data.model.fechaDeSalida
import com.example.mixtapp.ui.screens.search.components.CriteriosDeBusqueda
import com.example.mixtapp.ui.screens.search.components.SearchCategoryCardsRow
import com.example.mixtapp.ui.screens.search.components.SelectedCategoryHeader
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.AppTextField
import com.example.mixtapp.ui.utils.BarraSuperior
import com.example.mixtapp.ui.utils.CalificacionDeLaComunidad
import com.example.mixtapp.ui.utils.EmptyMessage
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.EspacioDelFab
import com.example.mixtapp.ui.utils.ItemRow
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.ScreenTitle
import com.example.mixtapp.ui.utils.SectionTitle
import com.example.mixtapp.ui.utils.TonoDeFondo
import com.example.mixtapp.ui.utils.UserRow

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    onItemClick: (String, String) -> Unit,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // El catalogo llega en vivo y el ViewModel ya entrega los resultados filtrados
    val state by searchViewModel.uiState.collectAsState()

    SearchScreenContent(
        isLoading = state.isLoading,
        errorMessageRes = state.errorMessageRes,
        query = state.query,
        ordenes = state.ordenes,
        generos = state.generos,
        categoriaElegida = state.categoriaElegida,
        ordenaPorFecha = state.ordenaPorFecha,
        masRecientesPrimero = state.masRecientesPrimero,
        resultados = state.resultados,
        usuarios = state.usuarios,
        errorSecundarioRes = state.errorCategoriasRes ?: state.errorUsuariosRes,
        onQueryChange = { searchViewModel.updateQuery(texto = it) },
        onCategoryClick = { searchViewModel.selectCategory(id = it) },
        onClearCategory = { searchViewModel.clearCategory() },
        onInvertirOrden = { searchViewModel.invertirOrden() },
        onItemClick = onItemClick,
        onUserClick = onUserClick,
        modifier = modifier,
    )
}

@Composable
fun SearchScreenContent(
    isLoading: Boolean,
    @StringRes errorMessageRes: Int?,
    query: String,
    ordenes: List<SearchCategoryUi>,
    generos: List<SearchCategoryUi>,
    categoriaElegida: SearchCategoryUi?,
    ordenaPorFecha: Boolean,
    masRecientesPrimero: Boolean,
    resultados: List<ItemConResenasUi>,
    usuarios: List<UsuarioUi>,
    @StringRes errorSecundarioRes: Int?,
    onQueryChange: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onClearCategory: () -> Unit,
    onInvertirOrden: () -> Unit,
    onItemClick: (String, String) -> Unit,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        AppBackground(tono = TonoDeFondo.Buscar)

        Column(modifier = Modifier.fillMaxSize()) {
            BarraSuperior {
                ScreenTitle(title = stringResource(R.string.search_title), onBackClick = null)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when {
                    isLoading -> LoadingBox()
                    // Sin catalogo no hay donde buscar: solo el motivo del error
                    errorMessageRes != null -> ErrorBox(messageRes = errorMessageRes)
                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = EspacioDelFab),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        item {
                            AppTextField(
                                value = query,
                                onValueChange = onQueryChange,
                                placeholder = stringResource(R.string.search_placeholder),
                                leadingIcon = Icons.Outlined.Search,
                                modifier = Modifier.testTag("txtBuscar"),
                            )

                            ErrorMessage(messageRes = errorSecundarioRes, modifier = Modifier.padding(top = 8.dp))
                        }

                        when {
                            // Con texto: los items que coinciden y, aparte, las personas
                            query.isNotBlank() -> {
                                if (resultados.isEmpty() && usuarios.isEmpty()) {
                                    item {
                                        EmptyMessage(messageRes = R.string.sin_resultados, modifier = Modifier.fillMaxWidth())
                                    }
                                }

                                listaDeResultados(resultados = resultados, conFecha = false, onItemClick = onItemClick)

                                if (usuarios.isNotEmpty()) {
                                    item {
                                        SectionTitle(
                                            title = stringResource(R.string.personas),
                                            modifier = Modifier.padding(top = 12.dp),
                                            actionText = null,
                                            onActionClick = null,
                                        )
                                    }

                                    items(usuarios) { usuario ->
                                        UserRow(usuario = usuario, onUserClick = onUserClick)
                                    }
                                }
                            }

                            // Con una tarjeta elegida: su titulo con la X y la lista en su orden
                            categoriaElegida != null -> {
                                item {
                                    SelectedCategoryHeader(
                                        titulo = when {
                                            !ordenaPorFecha -> stringResource(categoriaElegida.titleRes)
                                            masRecientesPrimero -> stringResource(R.string.lanzamientos_recientes)
                                            else -> stringResource(R.string.lanzamientos_antiguos)
                                        },
                                        orden = when {
                                            !ordenaPorFecha -> null
                                            masRecientesPrimero -> stringResource(R.string.orden_mas_recientes)
                                            else -> stringResource(R.string.orden_mas_antiguos)
                                        },
                                        onBackClick = onClearCategory,
                                        onOrdenClick = if (ordenaPorFecha) onInvertirOrden else null,
                                    )
                                }

                                if (resultados.isEmpty()) {
                                    item {
                                        EmptyMessage(messageRes = R.string.sin_resultados, modifier = Modifier.fillMaxWidth())
                                    }
                                }

                                listaDeResultados(resultados = resultados, conFecha = ordenaPorFecha, onItemClick = onItemClick)
                            }

                            // Sin texto ni tarjeta: las tarjetas de Explorar por y de Generos, dos por fila
                            else -> {
                                item {
                                    SectionTitle(
                                        title = stringResource(R.string.browse_by),
                                        modifier = Modifier.padding(top = 8.dp),
                                        actionText = null,
                                        onActionClick = null,
                                    )
                                }

                                item {
                                    CriteriosDeBusqueda(criterios = ordenes, onCategoryClick = onCategoryClick)
                                }

                                item {
                                    SectionTitle(
                                        title = stringResource(R.string.generos),
                                        modifier = Modifier.padding(top = 8.dp),
                                        actionText = null,
                                        onActionClick = null,
                                    )
                                }

                                items(generos.chunked(2)) { fila ->
                                    SearchCategoryCardsRow(categorias = fila, onCategoryClick = onCategoryClick)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Los items que coinciden: la misma lista con texto y con una tarjeta elegida. Cada fila lleva la
// calificacion de la comunidad o, en los lanzamientos, su fecha de salida, que es lo que los ordena
private fun LazyListScope.listaDeResultados(
    resultados: List<ItemConResenasUi>,
    conFecha: Boolean,
    onItemClick: (String, String) -> Unit,
) {
    items(resultados) { itemConResenas ->
        ItemRow(
            item = itemConResenas.item,
            onClick = { onItemClick(itemConResenas.item.tipo, itemConResenas.item.id) },
            modifier = Modifier.testTag("resultado_${itemConResenas.item.id}"),
        ) {
            if (conFecha) {
                Text(
                    text = itemConResenas.item.fechaDeSalida,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                )
            } else {
                CalificacionDeLaComunidad(rating = itemConResenas.rating, ratingCount = itemConResenas.ratingCount)
            }
        }
    }
}

@Composable
@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
fun SearchScreenPreview() {
    MixtappTheme(darkTheme = true) {
        SearchScreenContent(
            isLoading = false,
            errorMessageRes = null,
            query = "",
            ordenes = LocalSearchCategoriesProvider.categories.filter { !it.esGenero },
            generos = LocalSearchCategoriesProvider.categories.filter { it.esGenero },
            categoriaElegida = null,
            ordenaPorFecha = false,
            masRecientesPrimero = true,
            resultados = emptyList(),
            usuarios = emptyList(),
            errorSecundarioRes = null,
            onQueryChange = {},
            onCategoryClick = {},
            onClearCategory = {},
            onInvertirOrden = {},
            onItemClick = { _, _ -> },
            onUserClick = {},
        )
    }
}

@Composable
@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
fun SearchScreenResultadosPreview() {
    MixtappTheme(darkTheme = true) {
        SearchScreenContent(
            isLoading = false,
            errorMessageRes = null,
            query = "li",
            ordenes = emptyList(),
            generos = emptyList(),
            categoriaElegida = null,
            ordenaPorFecha = false,
            masRecientesPrimero = true,
            resultados = LocalPreviewProvider.catalogo,
            usuarios = listOf(LocalPreviewProvider.otroUsuario),
            errorSecundarioRes = null,
            onQueryChange = {},
            onCategoryClick = {},
            onClearCategory = {},
            onInvertirOrden = {},
            onItemClick = { _, _ -> },
            onUserClick = {},
        )
    }
}
