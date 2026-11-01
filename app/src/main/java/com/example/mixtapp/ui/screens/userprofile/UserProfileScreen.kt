package com.example.mixtapp.ui.screens.userprofile

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
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.data.model.UsuarioUi
import com.example.mixtapp.ui.screens.userprofile.components.FollowButton
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.EmptyMessage
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.FavoriteSection
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.ProfileHeader
import com.example.mixtapp.ui.utils.ReviewCard
import com.example.mixtapp.ui.utils.ScreenTitle
import com.example.mixtapp.ui.utils.SectionTitle

@Composable
fun UserProfileScreen(
    userId: String,
    userProfileViewModel: UserProfileViewModel,
    onItemClick: (String, String) -> Unit,
    onCommentsClick: (String) -> Unit,
    onSeguidoresClick: (String) -> Unit,
    onSeguidosClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by userProfileViewModel.uiState.collectAsState()

    // Necesita el id que llega por la ruta, asi que se pide en un LaunchedEffect. Las banderas del
    // ViewModel evitan un segundo listener al volver de la lista de seguidores
    LaunchedEffect(Unit) {
        userProfileViewModel.getUserProfile(userId = userId)
        userProfileViewModel.getUserReviews(userId = userId)
        userProfileViewModel.getUserFavoritos(userId = userId)
    }

    // Con una copia local, Kotlin sabe que no es nula despues de revisarla: no hace falta el !!
    val usuario = state.usuario

    Box(modifier = modifier.fillMaxSize()) {
        AppBackground()

        when {
            state.isLoading -> {
                LoadingBox()
            }
            // Sin el perfil no hay nada que mostrar: el motivo del error y la flecha para volver
            usuario == null -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    ScreenTitle(
                        title = stringResource(R.string.nav_profile),
                        onBackClick = onBackClick,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    ErrorBox(messageRes = state.errorMessageRes ?: R.string.perfil_no_encontrado)
                }
            }
            else -> UserProfileScreenContent(
                usuario = usuario,
                favoritos = state.favoritos,
                errorFavoritosRes = state.errorFavoritosRes,
                reviews = state.reviews,
                cargandoResenas = state.cargandoResenas,
                errorResenasRes = state.errorResenasRes,
                errorSeguimientoRes = state.errorSeguimientoRes,
                errorLikeRes = state.errorLikeRes,
                onFollowClick = { userProfileViewModel.followOrUnfollow() },
                onSeguidoresClick = { onSeguidoresClick(userId) },
                onSeguidosClick = { onSeguidosClick(userId) },
                onItemClick = { item -> onItemClick(item.tipo, item.id) },
                onLikeClick = { userProfileViewModel.sendOrDeleteLike(reviewId = it) },
                onCommentsClick = onCommentsClick,
                onBackClick = onBackClick,
            )
        }
    }
}

@Composable
fun UserProfileScreenContent(
    usuario: UsuarioUi,
    favoritos: List<ItemResumenUi>,
    @StringRes errorFavoritosRes: Int?,
    reviews: List<ReviewUi>,
    cargandoResenas: Boolean,
    @StringRes errorResenasRes: Int?,
    @StringRes errorSeguimientoRes: Int?,
    @StringRes errorLikeRes: Int?,
    onFollowClick: () -> Unit,
    onSeguidoresClick: () -> Unit,
    onSeguidosClick: () -> Unit,
    onItemClick: (ItemResumenUi) -> Unit,
    onLikeClick: (String) -> Unit,
    onCommentsClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Toda la pantalla es una sola lista: el encabezado sube con sus resenas
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ScreenTitle(
                title = stringResource(R.string.nav_profile),
                onBackClick = onBackClick
            )
        }

        item {
            ProfileHeader(
                nombre = usuario.nombre,
                username = usuario.username,
                bio = usuario.bio,
                fotoUrl = usuario.fotoUrl,
                seguidores = usuario.seguidores,
                seguidos = usuario.seguidos,
                onSeguidoresClick = onSeguidoresClick,
                onSeguidosClick = onSeguidosClick,
            ) {
                // Desde una lista se puede llegar a mi propio perfil publico: ahi no se sigue a nadie
                if (!usuario.esMiPerfil) {
                    FollowButton(
                        siguiendo = usuario.siguiendo,
                        onFollowClick = onFollowClick,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                ErrorMessage(
                    messageRes = errorSeguimientoRes,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        item {
            FavoriteSection(
                favoritos = favoritos,
                errorMessageRes = errorFavoritosRes,
                vaciaRes = R.string.sin_favoritos_usuario,
                onItemClick = onItemClick
            )
        }

        item {
            Column {
                SectionTitle(title = stringResource(R.string.reviews))

                // Un like que fallo
                ErrorMessage(messageRes = errorLikeRes)
            }
        }

        when {
            // Mientras llegan sus resenas, la carga va donde estara la lista
            cargandoResenas -> item {
                LoadingBox(modifier = Modifier.padding(vertical = 24.dp))
            }
            errorResenasRes != null -> item {
                ErrorMessage(messageRes = errorResenasRes)
            }
            reviews.isEmpty() -> item {
                EmptyMessage(messageRes = R.string.sin_resenas_usuario, modifier = Modifier.fillMaxWidth())
            }
            else -> items(reviews) { review ->
                ReviewCard(
                    review = review,
                    // Ya estoy en el perfil de quien la escribio
                    onAuthorClick = {},
                    onLikeClick = onLikeClick,
                    onCommentsClick = onCommentsClick,
                    mostrarItem = true,
                    onItemClick = onItemClick,
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
@Composable
fun UserProfileScreenPreview() {
    MixtappTheme(darkTheme = true) {
        Box {
            AppBackground()

            UserProfileScreenContent(
                usuario = LocalPreviewProvider.otroUsuario,
                favoritos = LocalPreviewProvider.favoritos,
                errorFavoritosRes = null,
                reviews = listOf(LocalPreviewProvider.review),
                cargandoResenas = false,
                errorResenasRes = null,
                errorSeguimientoRes = null,
                errorLikeRes = null,
                onFollowClick = {},
                onSeguidoresClick = {},
                onSeguidosClick = {},
                onItemClick = {},
                onLikeClick = {},
                onCommentsClick = {},
                onBackClick = {},
            )
        }
    }
}
