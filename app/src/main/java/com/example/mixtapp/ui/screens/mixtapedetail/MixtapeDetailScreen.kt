package com.example.mixtapp.ui.screens.mixtapedetail

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.MixtapeUi
import com.example.mixtapp.data.model.toItemResumenUi
import com.example.mixtapp.ui.screens.mixtapedetail.components.MixtapeItemRow
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.EmptyMessage
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.ScreenTitle

@Composable
fun MixtapeDetailScreen(
    mixtapeId: String,
    mixtapeDetailViewModel: MixtapeDetailViewModel,
    onItemClick: (tipo: String, itemId: String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by mixtapeDetailViewModel.uiState.collectAsState()

    // Necesita el id que llega por la ruta, asi que empieza a escuchar en un LaunchedEffect
    LaunchedEffect(Unit) {
        mixtapeDetailViewModel.getMixtape(id = mixtapeId)
    }

    // Cuando la borro se vuelve a la pantalla anterior
    LaunchedEffect(state.eliminada) {
        if (state.eliminada) onBackClick()
    }

    MixtapeDetailScreenContent(
        mixtape = state.mixtape,
        isLoading = state.isLoading,
        errorMessageRes = state.errorMessageRes,
        errorAccionRes = state.errorAccionRes,
        onItemClick = { item -> onItemClick(item.tipo, item.id) },
        onQuitarClick = { mixtapeDetailViewModel.quitarItem(itemId = it) },
        onEliminarClick = { mixtapeDetailViewModel.eliminarMixtape() },
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
fun MixtapeDetailScreenContent(
    mixtape: MixtapeUi?,
    isLoading: Boolean,
    @StringRes errorMessageRes: Int?,
    @StringRes errorAccionRes: Int?,
    onItemClick: (ItemResumenUi) -> Unit,
    onQuitarClick: (String) -> Unit,
    onEliminarClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("mixtapeDetailScreen")
    ) {
        AppBackground()

        when {
            isLoading -> LoadingBox()
            // Sin mixtape no hay nada que mostrar: el error ocupa la pantalla
            mixtape == null -> Column(modifier = Modifier.fillMaxSize()) {
                ScreenTitle(title = "", onBackClick = onBackClick)
                ErrorBox(messageRes = errorMessageRes ?: R.string.mixtape_no_encontrada)
            }
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ScreenTitle(
                            title = mixtape.nombre,
                            onBackClick = onBackClick,
                            modifier = Modifier.weight(1f),
                        )

                        // Solo quien la creo la puede borrar
                        if (mixtape.isMine) {
                            IconButton(
                                onClick = onEliminarClick,
                                modifier = Modifier.testTag("btnEliminarMixtape"),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = stringResource(R.string.eliminar_mixtape),
                                    tint = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }
                }

                item {
                    Column {
                        if (mixtape.descripcion.isNotEmpty()) {
                            Text(
                                text = mixtape.descripcion,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }

                        Text(
                            text = stringResource(R.string.creada_por, mixtape.usuarioNombre),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium,
                        )

                        ErrorMessage(messageRes = errorAccionRes)
                    }
                }

                if (mixtape.items.isEmpty()) {
                    item {
                        EmptyMessage(messageRes = R.string.sin_items_mixtape, modifier = Modifier.fillMaxWidth())
                    }
                }

                items(mixtape.items, key = { it.id }) { item ->
                    MixtapeItemRow(
                        item = item,
                        puedeQuitar = mixtape.isMine,
                        onClick = { onItemClick(item) },
                        onQuitarClick = { onQuitarClick(item.id) },
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MixtapeDetailScreenPreview() {
    MixtappTheme(darkTheme = true) {
        MixtapeDetailScreenContent(
            mixtape = MixtapeUi(
                id = "mixtape_1",
                nombre = "Para entrenar",
                descripcion = "Lo que suena en el gimnasio",
                usuarioId = "yo",
                usuarioNombre = "Andres",
                usuarioFotoUrl = "",
                items = listOf(LocalPreviewProvider.fromZero.toItemResumenUi(), LocalPreviewProvider.karolG.toItemResumenUi()),
                isMine = true,
                timeAgo = "2 d",
            ),
            isLoading = false,
            errorMessageRes = null,
            errorAccionRes = null,
            onItemClick = {},
            onQuitarClick = {},
            onEliminarClick = {},
            onBackClick = {},
        )
    }
}
