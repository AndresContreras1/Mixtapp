package com.example.mixtapp.ui.screens.agregaramixtape

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.MixtapeUi
import com.example.mixtapp.ui.screens.mixtapes.components.MixtapeCard
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.AppButton
import com.example.mixtapp.ui.utils.EmptyMessage
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.ScreenTitle

@Composable
fun AgregarAMixtapeScreen(
    tipo: String,
    itemId: String,
    agregarAMixtapeViewModel: AgregarAMixtapeViewModel,
    onCrearMixtapeClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by agregarAMixtapeViewModel.uiState.collectAsState()

    // Necesita el tipo y el id que llegan por la ruta, asi que lo pide en un LaunchedEffect
    LaunchedEffect(Unit) {
        agregarAMixtapeViewModel.getItem(tipo = tipo, itemId = itemId)
    }

    // Cuando el item queda agregado se vuelve al detalle
    LaunchedEffect(state.navigate) {
        if (state.navigate) onBackClick()
    }

    AgregarAMixtapeScreenContent(
        item = state.item,
        mixtapes = state.mixtapes,
        isLoading = state.isLoading,
        errorMessageRes = state.errorMessageRes,
        errorAccionRes = state.errorAccionRes,
        onMixtapeClick = { agregarAMixtapeViewModel.agregarA(mixtapeId = it) },
        onCrearMixtapeClick = onCrearMixtapeClick,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
fun AgregarAMixtapeScreenContent(
    item: ItemUi?,
    mixtapes: List<MixtapeUi>,
    isLoading: Boolean,
    @StringRes errorMessageRes: Int?,
    @StringRes errorAccionRes: Int?,
    onMixtapeClick: (String) -> Unit,
    onCrearMixtapeClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("agregarAMixtapeScreen")
    ) {
        AppBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            // El titulo queda fijo para poder volver aunque falle la carga
            ScreenTitle(
                title = stringResource(R.string.agregar_a_mixtape),
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
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        Text(
                            text = item?.titulo ?: stringResource(R.string.elige_mixtape),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        ErrorMessage(messageRes = errorAccionRes)
                    }

                    if (mixtapes.isEmpty()) {
                        item {
                            EmptyMessage(messageRes = R.string.sin_mixtapes, modifier = Modifier.fillMaxWidth())
                        }
                    }

                    // Las que ya tienen el item lo dicen al final; tocarlas no lo repite
                    items(mixtapes, key = { it.id }) { mixtape ->
                        val yaEsta = mixtape.items.any { it.id == item?.id }

                        MixtapeCard(
                            mixtape = mixtape,
                            onClick = { if (!yaEsta) onMixtapeClick(mixtape.id) },
                            etiqueta = if (yaEsta) stringResource(R.string.ya_esta_en_mixtape) else null,
                        )
                    }

                    item {
                        AppButton(
                            texto = stringResource(R.string.nueva_mixtape),
                            onClick = onCrearMixtapeClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btnNuevaMixtape"),
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AgregarAMixtapeScreenPreview() {
    MixtappTheme(darkTheme = true) {
        AgregarAMixtapeScreenContent(
            item = LocalPreviewProvider.fromZero,
            mixtapes = emptyList(),
            isLoading = false,
            errorMessageRes = null,
            errorAccionRes = null,
            onMixtapeClick = {},
            onCrearMixtapeClick = {},
            onBackClick = {},
        )
    }
}
