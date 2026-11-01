package com.example.mixtapp.ui.screens.search

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
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
import com.example.mixtapp.ui.screens.search.components.ResultadoItemRow
import com.example.mixtapp.ui.screens.search.components.SearchCategoryCardsRow
import com.example.mixtapp.ui.screens.search.components.SelectedCategoryHeader
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.AppTextField
import com.example.mixtapp.ui.utils.EmptyMessage
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.ScreenTitle
import com.example.mixtapp.ui.utils.SectionTitle
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
        resultados = state.resultados,
        usuarios = state.usuarios,
        errorSecundarioRes = state.errorCategoriasRes ?: state.errorUsuariosRes,
        onQueryChange = { searchViewModel.updateQuery(texto = it) },
        onCategoryClick = { searchViewModel.selectCategory(id = it) },
        onClearCategory = { searchViewModel.clearCategory() },
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
    resultados: List<ItemConResenasUi>,
    usuarios: List<UsuarioUi>,
    @StringRes errorSecundarioRes: Int?,
    onQueryChange: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onClearCategory: () -> Unit,
    onItemClick: (String, String) -> Unit,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        AppBackground()

        when {
            isLoading -> LoadingBox()
            // Sin catalogo no hay donde buscar: solo el motivo del error
            errorMessageRes != null -> ErrorBox(messageRes = errorMessageRes)
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    ScreenTitle(title = stringResource(R.string.search_title))
                }

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

                        items(resultados) { itemConResenas ->
                            ResultadoItemRow(
                                itemConResenas = itemConResenas,
                                onClick = { onItemClick(itemConResenas.item.tipo, itemConResenas.item.id) },
                                modifier = Modifier.testTag("resultado_${itemConResenas.item.id}"),
                            )
                        }

                        if (usuarios.isNotEmpty()) {
                            item {
                                SectionTitle(title = stringResource(R.string.personas), modifier = Modifier.padding(top = 12.dp))
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
                                titulo = stringResource(categoriaElegida.titleRes),
                                onClearClick = onClearCategory,
                            )
                        }

                        if (resultados.isEmpty()) {
                            item {
                                EmptyMessage(messageRes = R.string.sin_resultados, modifier = Modifier.fillMaxWidth())
                            }
                        }

                        items(resultados) { itemConResenas ->
                            ResultadoItemRow(
                                itemConResenas = itemConResenas,
                                onClick = { onItemClick(itemConResenas.item.tipo, itemConResenas.item.id) },
                                modifier = Modifier.testTag("resultado_${itemConResenas.item.id}"),
                            )
                        }
                    }

                    // Sin texto ni tarjeta: las tarjetas de Explorar por y de Generos, dos por fila
                    else -> {
                        item {
                            SectionTitle(title = stringResource(R.string.browse_by), modifier = Modifier.padding(top = 8.dp))
                        }

                        items(ordenes.chunked(2)) { fila ->
                            SearchCategoryCardsRow(categorias = fila, onCategoryClick = onCategoryClick)
                        }

                        item {
                            SectionTitle(title = stringResource(R.string.generos), modifier = Modifier.padding(top = 8.dp))
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
            resultados = emptyList(),
            usuarios = emptyList(),
            errorSecundarioRes = null,
            onQueryChange = {},
            onCategoryClick = {},
            onClearCategory = {},
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
            resultados = LocalPreviewProvider.catalogo,
            usuarios = listOf(LocalPreviewProvider.otroUsuario),
            errorSecundarioRes = null,
            onQueryChange = {},
            onCategoryClick = {},
            onClearCategory = {},
            onItemClick = { _, _ -> },
            onUserClick = {},
        )
    }
}
