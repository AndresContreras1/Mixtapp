package com.example.mixtapp.ui.screens.followlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalUsuarioProvider
import com.example.mixtapp.data.model.UsuarioUi
import com.example.mixtapp.ui.screens.followlist.components.FollowUserRow
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.theme.displayFontFamily
import com.example.mixtapp.ui.utils.BackButton
import com.example.mixtapp.ui.utils.LoadingBox

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

    FollowListScreenContent(
        titulo = stringResource(if (tipo == LISTA_SEGUIDORES) R.string.titulo_seguidores else R.string.titulo_seguidos),
        vacia = stringResource(if (tipo == LISTA_SEGUIDORES) R.string.sin_seguidores else R.string.sin_seguidos),
        usuarios = state.usuarios,
        isLoading = state.isLoading,
        errorMessageRes = state.errorMessageRes,
        onUserClick = onUserClick,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
fun FollowListScreenContent(
    titulo: String,
    // El texto que se muestra si la lista esta vacia
    vacia: String,
    usuarios: List<UsuarioUi>,
    isLoading: Boolean,
    errorMessageRes: Int?,
    onUserClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(onBackClick = onBackClick)

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = titulo,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 24.sp,
                fontFamily = displayFontFamily,
                fontWeight = FontWeight.Bold
            )
        }

        when {
            isLoading -> {
                LoadingBox(modifier = Modifier)
            }
            errorMessageRes != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = stringResource(errorMessageRes))
                }
            }
            usuarios.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = vacia,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    items(usuarios.size) { index ->
                        val usuario = usuarios[index]

                        FollowUserRow(
                            usuario = usuario,
                            onUserClick = { onUserClick(usuario.id) },
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
@Composable
fun FollowListScreenPreview() {
    MixtappTheme(darkTheme = true) {
        FollowListScreenContent(
            titulo = stringResource(R.string.titulo_seguidores),
            vacia = stringResource(R.string.sin_seguidores),
            usuarios = LocalUsuarioProvider.usuarios,
            isLoading = false,
            errorMessageRes = null,
            onUserClick = {},
            onBackClick = {},
        )
    }
}
