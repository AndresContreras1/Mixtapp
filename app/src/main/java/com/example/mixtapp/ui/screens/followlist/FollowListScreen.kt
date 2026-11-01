package com.example.mixtapp.ui.screens.followlist

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
import com.example.mixtapp.data.model.UsuarioUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.EmptyMessage
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.ScreenTitle
import com.example.mixtapp.ui.utils.UserRow

// Recibe el id del usuario y que lista mostrar; el ViewModel se encarga de buscarla
@Composable
fun FollowListScreen(
    userId: String,
    tipo: String,
    followListViewModel: FollowListViewModel,
    onUserClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by followListViewModel.uiState.collectAsState()

    // Necesita los datos que llegan por la ruta, asi que se pide en un LaunchedEffect
    LaunchedEffect(Unit) {
        followListViewModel.getUsuarios(userId = userId, tipo = tipo)
    }

    val esSeguidores = tipo == LISTA_SEGUIDORES

    // Con una copia local, Kotlin sabe que no es nulo despues de revisarlo: no hace falta el !!
    val errorMessageRes = state.errorMessageRes

    Box(modifier = modifier.fillMaxSize()) {
        AppBackground()

        // El titulo y la flecha quedan fijos arriba: tambien mientras carga o si hay un error
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenTitle(
                title = stringResource(if (esSeguidores) R.string.titulo_seguidores else R.string.titulo_seguidos),
                onBackClick = onBackClick,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            when {
                state.isLoading -> {
                    LoadingBox()
                }
                errorMessageRes != null && state.usuarios.isEmpty() -> {
                    ErrorBox(messageRes = errorMessageRes)
                }
                else -> FollowListScreenContent(
                    usuarios = state.usuarios,
                    vaciaRes = if (esSeguidores) R.string.sin_seguidores else R.string.sin_seguidos,
                    onUserClick = onUserClick,
                )
            }
        }
    }
}

@Composable
fun FollowListScreenContent(
    usuarios: List<UsuarioUi>,
    // El texto que se muestra si la lista esta vacia
    @StringRes vaciaRes: Int,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (usuarios.isEmpty()) {
            item {
                EmptyMessage(messageRes = vaciaRes, modifier = Modifier.fillMaxWidth())
            }
        }

        items(usuarios) { usuario ->
            UserRow(usuario = usuario, onUserClick = onUserClick)
        }
    }
}

@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
@Composable
fun FollowListScreenPreview() {
    MixtappTheme(darkTheme = true) {
        Box {
            AppBackground()

            FollowListScreenContent(
                usuarios = LocalPreviewProvider.following.seguidos,
                vaciaRes = R.string.sin_seguidores,
                onUserClick = {},
            )
        }
    }
}
