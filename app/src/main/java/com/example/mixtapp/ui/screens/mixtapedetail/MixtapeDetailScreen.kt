package com.example.mixtapp.ui.screens.mixtapedetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.screens.profile.components.GUARDADOS_POR_FILA
import com.example.mixtapp.ui.screens.profile.components.SavedItemsRow
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.EmptyMessage
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.ScreenTitle

@Composable
fun MixtapeDetailScreen(
    mixtapeId: String,
    mixtapeDetailViewModel: MixtapeDetailViewModel,
    onBackClick: () -> Unit,
    onItemClick: (tipo: String, itemId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by mixtapeDetailViewModel.uiState.collectAsState()

    LaunchedEffect(mixtapeId) {
        mixtapeDetailViewModel.escucharMixtape(mixtapeId)
    }

    Box(modifier = modifier.fillMaxSize()) {
        AppBackground()

        when {
            state.isLoading -> LoadingBox()
            state.errorMessageRes != null -> ErrorBox(messageRes = state.errorMessageRes!!)
            state.mixtape == null -> ErrorBox(messageRes = R.string.contenido_no_encontrado)
            else -> {
                val mixtape = state.mixtape!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ScreenTitle(
                            title = mixtape.nombre,
                            onBackClick = onBackClick,
                            modifier = Modifier.weight(1f)
                        )

                        if (mixtape.isMine) {
                            IconButton(onClick = {
                                mixtapeDetailViewModel.eliminarMixtape(onBackClick)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.eliminar_mixtape),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    if (mixtape.descripcion.isNotBlank()) {
                        Text(
                            text = mixtape.descripcion,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    Text(
                        text = "Creado por ${mixtape.usuarioNombre}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (mixtape.items.isEmpty()) {
                        EmptyMessage(messageRes = R.string.sin_items_mixtape)
                    } else {
                        val filas = mixtape.items.chunked(GUARDADOS_POR_FILA)
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(filas) { fila ->
                                SavedItemsRow(
                                    items = fila,
                                    onItemClick = { onItemClick(it.tipo, it.id) },
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
