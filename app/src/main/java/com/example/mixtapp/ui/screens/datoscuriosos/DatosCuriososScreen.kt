package com.example.mixtapp.ui.screens.datoscuriosos

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.HistoriaUi
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.ui.screens.datoscuriosos.components.DatoCuriosoCard
import com.example.mixtapp.ui.screens.detail.components.ItemHeader
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.EmptyMessage
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.SectionTitle

// Los datos curiosos de un album, una cancion o un artista, por secciones, debajo del mismo
// encabezado del detalle
@Composable
fun DatosCuriososScreen(
    tipo: String,
    itemId: String,
    datosCuriososViewModel: DatosCuriososViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by datosCuriososViewModel.uiState.collectAsState()

    // Necesita el tipo y el id que llegan por la ruta
    LaunchedEffect(Unit) {
        datosCuriososViewModel.getItem(tipo = tipo, itemId = itemId)
    }

    DatosCuriososScreenContent(
        isLoading = state.isLoading,
        item = state.item,
        historia = state.historia,
        errorMessageRes = state.errorMessageRes,
        errorHistoriaRes = state.errorHistoriaRes,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
fun DatosCuriososScreenContent(
    isLoading: Boolean,
    item: ItemUi?,
    historia: HistoriaUi?,
    @StringRes errorMessageRes: Int?,
    @StringRes errorHistoriaRes: Int?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        AppBackground()

        when {
            isLoading -> LoadingBox()
            item == null -> ErrorBox(messageRes = errorMessageRes ?: R.string.album_no_encontrado)
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            ) {
                item {
                    ItemHeader(item = item, onBackClick = onBackClick)
                }

                item {
                    SectionTitle(
                        title = stringResource(R.string.datos_curiosos),
                        modifier = Modifier.padding(top = 28.dp, bottom = 12.dp),
                    )
                }

                // Sin historia: el item no tiene datos curiosos o no se pudieron cargar
                if (historia == null) {
                    item {
                        EmptyMessage(
                            messageRes = errorHistoriaRes ?: R.string.sin_datos_curiosos,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                } else {
                    items(historia.secciones) { seccion ->
                        DatoCuriosoCard(
                            titulo = seccion.titulo,
                            texto = seccion.texto,
                            modifier = Modifier.padding(bottom = 12.dp),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
@Composable
fun DatosCuriososScreenPreview() {
    MixtappTheme(darkTheme = true) {
        DatosCuriososScreenContent(
            isLoading = false,
            item = LocalPreviewProvider.fromZero,
            historia = LocalPreviewProvider.historiaFromZero,
            errorMessageRes = null,
            errorHistoriaRes = null,
            onBackClick = {},
        )
    }
}
