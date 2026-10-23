package com.example.mixtapp.ui.screens.userprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalMyReviewsProvider
import com.example.mixtapp.data.local.LocalUsuarioProvider
import com.example.mixtapp.data.model.MyReviewUi
import com.example.mixtapp.data.model.UsuarioUi
import com.example.mixtapp.ui.screens.userprofile.components.UserListDialog
import com.example.mixtapp.ui.screens.userprofile.components.UserProfileHeader
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.MyReviewCard

@Composable
fun UserProfileScreen(
    userId: String,
    userProfileViewModel: UserProfileViewModel,
    onReviewClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by userProfileViewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        userProfileViewModel.getUserProfile(userId = userId)
        userProfileViewModel.getUserReviews(userId = userId)
    }

    when {
        state.isLoading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        state.usuario == null -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(state.errorMessageRes ?: R.string.perfil_no_encontrado))
            }
        }
        else -> UserProfileScreenContent(
            usuario = state.usuario!!,
            seguidores = state.seguidores,
            seguidos = state.seguidos,
            tipoListaAbierta = state.tipoListaAbierta,
            onSeguidoresClick = userProfileViewModel::abrirSeguidores,
            onSeguidosClick = userProfileViewModel::abrirSeguidos,
            onCerrarLista = userProfileViewModel::cerrarLista,
            cambiandoSeguimiento = state.cambiandoSeguimiento,
            onFollowClick = userProfileViewModel::seguirDejarDeSeguir,
            reviews = state.reviews,
            cargandoResenas = state.cargandoResenas,
            errorMessageRes = state.errorSeguimientoRes ?: state.errorResenasRes ?: state.errorMessageRes,
            onReviewClick = onReviewClick,
            onUserClick = onUserClick,
            onBackClick = onBackClick,
            modifier = modifier
        )
    }
}

@Composable
fun UserProfileScreenContent(
    usuario: UsuarioUi,
    seguidores: List<UsuarioUi>,
    seguidos: List<UsuarioUi>,
    tipoListaAbierta: TipoLista?,
    onSeguidoresClick: () -> Unit,
    onSeguidosClick: () -> Unit,
    onCerrarLista: () -> Unit,
    cambiandoSeguimiento: Boolean = false,
    onFollowClick: () -> Unit = {},
    reviews: List<MyReviewUi>,
    cargandoResenas: Boolean,
    errorMessageRes: Int?,
    onReviewClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
        ) {
            item {
                UserProfileHeader(
                    usuario = usuario,
                    seguidoresCount = seguidores.size,
                    seguidosCount = seguidos.size,
                    onSeguidoresClick = onSeguidoresClick,
                    onSeguidosClick = onSeguidosClick,
                    cambiandoSeguimiento = cambiandoSeguimiento,
                    onFollowClick = onFollowClick,
                    reviewsCount = reviews.size,
                    cargandoResenas = cargandoResenas,
                    onBackClick = onBackClick,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                ErrorMessage(
                    messageRes = errorMessageRes,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Mientras llegan sus resenas, la carga va donde estara la lista
            if (cargandoResenas) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            items(reviews.size) { index ->
                MyReviewCard(
                    review = reviews[index],
                    onReviewClick = onReviewClick,
                    showEditButton = false,
                    onEditClick = {},
                    onDeleteClick = {},
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

        if (tipoListaAbierta != null) {
            val titulo = if (tipoListaAbierta == TipoLista.SEGUIDORES) "Seguidores" else "Seguidos"
            val lista = if (tipoListaAbierta == TipoLista.SEGUIDORES) seguidores else seguidos

            UserListDialog(
                titulo = titulo,
                usuarios = lista,
                onUserClick = onUserClick,
                onDismiss = onCerrarLista
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
@Composable
fun UserProfileScreenPreview() {
    MixtappTheme(darkTheme = true) {
        UserProfileScreenContent(
            usuario = LocalUsuarioProvider.usuario,
            seguidores = emptyList(),
            seguidos = emptyList(),
            tipoListaAbierta = null,
            onSeguidoresClick = {},
            onSeguidosClick = {},
            onCerrarLista = {},
            reviews = LocalMyReviewsProvider.reviews,
            cargandoResenas = false,
            errorMessageRes = null,
            onReviewClick = {},
            onUserClick = {},
            onBackClick = {}
        )
    }
}
