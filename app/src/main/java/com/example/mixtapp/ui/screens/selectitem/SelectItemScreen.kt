package com.example.mixtapp.ui.screens.selectitem

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.mixtapp.data.model.ItemConResenasUi
import com.example.mixtapp.ui.screens.search.components.ResultadoItemRow
import com.example.mixtapp.ui.screens.selectitem.model.SelectItemFilterUi
import com.example.mixtapp.ui.screens.selectitem.model.selectItemFilters
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.AppChip
import com.example.mixtapp.ui.utils.AppTextField
import com.example.mixtapp.ui.utils.EmptyMessage
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.ScreenTitle

// El boton + de la barra abre esta pantalla para elegir que calificar antes de escribir la resena
@Composable
fun SelectItemScreen(
    selectItemViewModel: SelectItemViewModel,
    onItemClick: (String, String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by selectItemViewModel.uiState.collectAsState()

    SelectItemScreenContent(
        isLoading = state.isLoading,
        errorMessageRes = state.errorMessageRes,
        query = state.query,
        filters = state.filters,
        selectedFilterId = state.selectedFilterId,
        resultados = state.resultados,
        onQueryChange = { selectItemViewModel.updateQuery(texto = it) },
        onFilterSelected = { selectItemViewModel.updateSelectedFilter(filtroId = it) },
        onItemClick = onItemClick,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
fun SelectItemScreenContent(
    isLoading: Boolean,
    @StringRes errorMessageRes: Int?,
    query: String,
    filters: List<SelectItemFilterUi>,
    selectedFilterId: String,
    resultados: List<ItemConResenasUi>,
    onQueryChange: (String) -> Unit,
    onFilterSelected: (String) -> Unit,
    onItemClick: (String, String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        AppBackground()

        when {
            isLoading -> LoadingBox()
            // Sin catalogo no hay nada que elegir: solo el motivo del error
            errorMessageRes != null -> ErrorBox(messageRes = errorMessageRes)
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    ScreenTitle(title = stringResource(R.string.elegir_album_titulo), onBackClick = onBackClick)

                    Text(
                        text = stringResource(R.string.elegir_album_descripcion),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                item {
                    AppTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        placeholder = stringResource(R.string.elegir_item_placeholder),
                        leadingIcon = Icons.Outlined.Search,
                        modifier = Modifier.testTag("txtFiltrarItems"),
                    )
                }

                // Cuatro chips no caben en todos los celulares: la fila se desliza
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(filters) { filter ->
                            AppChip(
                                text = stringResource(filter.label),
                                isSelected = filter.id == selectedFilterId,
                                onClick = { onFilterSelected(filter.id) },
                                modifier = Modifier.testTag("tipo_${filter.id}"),
                            )
                        }
                    }
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
                        modifier = Modifier.testTag("elegir_${itemConResenas.item.id}"),
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
fun SelectItemScreenPreview() {
    MixtappTheme(darkTheme = true) {
        SelectItemScreenContent(
            isLoading = false,
            errorMessageRes = null,
            query = "",
            filters = selectItemFilters,
            selectedFilterId = selectItemFilters.first().id,
            resultados = LocalPreviewProvider.catalogo,
            onQueryChange = {},
            onFilterSelected = {},
            onItemClick = { _, _ -> },
            onBackClick = {},
        )
    }
}
