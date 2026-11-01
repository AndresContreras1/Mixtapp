package com.example.mixtapp.ui.screens.profile

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.ui.screens.followlist.LISTA_SEGUIDORES
import com.example.mixtapp.ui.screens.followlist.LISTA_SEGUIDOS
import com.example.mixtapp.ui.screens.profile.components.GUARDADOS_POR_FILA
import com.example.mixtapp.ui.screens.profile.components.LogoutButton
import com.example.mixtapp.ui.screens.profile.components.ProfileTabs
import com.example.mixtapp.ui.screens.profile.components.SavedItemsRow
import com.example.mixtapp.ui.screens.profile.model.PESTANA_GUARDADOS
import com.example.mixtapp.ui.screens.profile.model.PESTANA_PERFIL
import com.example.mixtapp.ui.screens.profile.model.ProfileTabUi
import com.example.mixtapp.ui.screens.profile.model.profileTabs
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
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    onSettingsClick: () -> Unit,
    logoutButtonPressed: () -> Unit,
    onItemClick: (String, String) -> Unit,
    onCommentsClick: (String) -> Unit,
    onEditReviewClick: (String, String) -> Unit,
    onFollowListClick: (userId: String, tipo: String) -> Unit,
    onSeeAllReviewsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by profileViewModel.uiState.collectAsState()

    // Cerrar sesion esta al final del perfil y tambien en el error, para que nadie quede atrapado
    val onLogoutClick = {
        profileViewModel.cerrarSesion()
        logoutButtonPressed()
    }

    // Los flujos se escuchan desde el init del ViewModel. Mis datos se piden otra vez al volver (por
    // ejemplo de Ajustes) por si mi documento no existia; la bandera evita un segundo listener
    LaunchedEffect(Unit) {
        profileViewModel.escucharMiPerfil()
    }

    // Con una copia local, Kotlin sabe que no es nulo despues de revisarlo: no hace falta el !!
    val errorMessageRes = state.errorMessageRes

    Box(modifier = modifier.fillMaxSize()) {
        AppBackground()

        when {
            state.isLoading -> {
                LoadingBox()
            }
            // Sin mis datos solo se muestra el motivo del error y la salida para cerrar sesion
            errorMessageRes != null && state.usuario.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ErrorBox(messageRes = errorMessageRes, modifier = Modifier.weight(1f))

                    LogoutButton(onLogoutClick = onLogoutClick)
                }
            }
            else -> ProfileScreenContent(
                usuario = state.usuario,
                nombre = state.nombre,
                bio = state.bio,
                instagram = state.instagram,
                instagramUrl = state.instagramUrl,
                seguidores = state.seguidores,
                seguidos = state.seguidos,
                profileImageUrl = state.profileImageUrl,
                subiendoImagen = state.subiendoImagen,
                errorImagenRes = state.errorImagenRes,
                // Si ya habia datos que mostrar, el error de mis datos sale como aviso
                avisoRes = state.avisoPerfilRes ?: errorMessageRes,
                tabs = state.tabs,
                selectedTabId = state.selectedTabId,
                favoritos = state.favoritos,
                errorFavoritosRes = state.errorFavoritosRes,
                resenasRecientes = state.resenasRecientes,
                errorResenasRes = state.errorResenasRes,
                errorAccionRes = state.errorAccionRes,
                guardados = state.guardados,
                errorGuardadosRes = state.errorGuardadosRes,
                onSettingsClick = onSettingsClick,
                // Mis contadores abren mi lista de seguidores o de seguidos con mi uid
                onSeguidoresClick = { onFollowListClick(state.miId, LISTA_SEGUIDORES) },
                onSeguidosClick = { onFollowListClick(state.miId, LISTA_SEGUIDOS) },
                onImagePicked = { profileViewModel.subirFotoDePerfil(uri = it) },
                onTabSelected = { profileViewModel.updateSelectedTab(tabId = it) },
                onItemClick = { item -> onItemClick(item.tipo, item.id) },
                onLikeClick = { profileViewModel.sendOrDeleteLike(reviewId = it) },
                onCommentsClick = onCommentsClick,
                // Editar abre Escribir resena con lo que califique
                onEditClick = { review -> onEditReviewClick(review.item.tipo, review.item.id) },
                onDeleteClick = { profileViewModel.eliminarResena(reviewId = it) },
                onSeeAllReviewsClick = onSeeAllReviewsClick,
                onLogoutClick = onLogoutClick,
            )
        }
    }
}

@Composable
fun ProfileScreenContent(
    usuario: String,
    nombre: String,
    bio: String,
    instagram: String,
    instagramUrl: String,
    seguidores: Int,
    seguidos: Int,
    profileImageUrl: String,
    subiendoImagen: Boolean,
    @StringRes errorImagenRes: Int?,
    @StringRes avisoRes: Int?,
    tabs: List<ProfileTabUi>,
    selectedTabId: String,
    favoritos: List<ItemResumenUi>,
    @StringRes errorFavoritosRes: Int?,
    resenasRecientes: List<ReviewUi>,
    @StringRes errorResenasRes: Int?,
    @StringRes errorAccionRes: Int?,
    guardados: List<ItemResumenUi>,
    @StringRes errorGuardadosRes: Int?,
    onSettingsClick: () -> Unit,
    onSeguidoresClick: () -> Unit,
    onSeguidosClick: () -> Unit,
    onImagePicked: (Uri) -> Unit,
    onTabSelected: (String) -> Unit,
    onItemClick: (ItemResumenUi) -> Unit,
    onLikeClick: (String) -> Unit,
    onCommentsClick: (String) -> Unit,
    onEditClick: (ReviewUi) -> Unit,
    onDeleteClick: (String) -> Unit,
    onSeeAllReviewsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Toda la pantalla es una sola lista: el encabezado y las pestanas suben con el contenido
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        // Abajo queda espacio para que el boton + no tape el ultimo elemento
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ScreenTitle(
                    title = stringResource(R.string.nav_profile),
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = stringResource(R.string.ajustes),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            ProfileHeader(
                nombre = nombre,
                username = usuario,
                bio = bio,
                fotoUrl = profileImageUrl,
                seguidores = seguidores,
                seguidos = seguidos,
                instagram = instagram,
                instagramUrl = instagramUrl,
                onSeguidoresClick = onSeguidoresClick,
                onSeguidosClick = onSeguidosClick,
            ) {
                ErrorMessage(
                    messageRes = avisoRes,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        item {
            Column {
                ProfileTabs(
                    tabs = tabs,
                    selectedTabId = selectedTabId,
                    onTabSelected = onTabSelected,
                    subiendoImagen = subiendoImagen,
                    onImagePicked = onImagePicked
                )

                // La foto que no se pudo subir, debajo de la pestana de Cambiar foto
                ErrorMessage(
                    messageRes = errorImagenRes,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        when (selectedTabId) {
            PESTANA_GUARDADOS -> {
                when {
                    errorGuardadosRes != null -> item {
                        ErrorMessage(messageRes = errorGuardadosRes)
                    }
                    guardados.isEmpty() -> item {
                        EmptyMessage(messageRes = R.string.sin_guardados, modifier = Modifier.fillMaxWidth())
                    }
                    // La cuadricula se arma con filas de tres: cada fila es un item de la lista
                    else -> items(guardados.chunked(GUARDADOS_POR_FILA)) { fila ->
                        SavedItemsRow(items = fila, onItemClick = onItemClick)
                    }
                }
            }
            else -> {
                item {
                    FavoriteSection(
                        favoritos = favoritos,
                        errorMessageRes = errorFavoritosRes,
                        vaciaRes = R.string.sin_favoritos,
                        onItemClick = onItemClick
                    )
                }

                item {
                    Column {
                        SectionTitle(
                            title = stringResource(R.string.actividad_reciente),
                            actionText = stringResource(R.string.ver_todas),
                            onActionClick = onSeeAllReviewsClick
                        )

                        // Un like o un borrado que fallo
                        ErrorMessage(messageRes = errorAccionRes)
                    }
                }

                when {
                    errorResenasRes != null -> item {
                        ErrorMessage(messageRes = errorResenasRes)
                    }
                    resenasRecientes.isEmpty() -> item {
                        EmptyMessage(messageRes = R.string.sin_mis_resenas, modifier = Modifier.fillMaxWidth())
                    }
                    else -> items(resenasRecientes) { review ->
                        ReviewCard(
                            review = review,
                            // Soy yo: tocar mi nombre no abre nada
                            onAuthorClick = {},
                            onLikeClick = onLikeClick,
                            onCommentsClick = onCommentsClick,
                            mostrarItem = true,
                            onItemClick = onItemClick,
                            onEditClick = onEditClick,
                            onDeleteClick = onDeleteClick,
                        )
                    }
                }

                item {
                    LogoutButton(
                        onLogoutClick = onLogoutClick,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
fun ProfileScreenPreview() {
    MixtappTheme(darkTheme = true) {
        val usuario = LocalPreviewProvider.usuario

        Box {
            AppBackground()

            ProfileScreenContent(
                usuario = usuario.username,
                nombre = usuario.nombre,
                bio = usuario.bio,
                instagram = usuario.instagram,
                instagramUrl = usuario.instagramUrl,
                seguidores = usuario.seguidores,
                seguidos = usuario.seguidos,
                profileImageUrl = usuario.fotoUrl,
                subiendoImagen = false,
                errorImagenRes = null,
                avisoRes = null,
                tabs = profileTabs,
                selectedTabId = PESTANA_PERFIL,
                favoritos = LocalPreviewProvider.favoritos,
                errorFavoritosRes = null,
                resenasRecientes = listOf(LocalPreviewProvider.miReview),
                errorResenasRes = null,
                errorAccionRes = null,
                guardados = LocalPreviewProvider.favoritos,
                errorGuardadosRes = null,
                onSettingsClick = {},
                onSeguidoresClick = {},
                onSeguidosClick = {},
                onImagePicked = {},
                onTabSelected = {},
                onItemClick = {},
                onLikeClick = {},
                onCommentsClick = {},
                onEditClick = {},
                onDeleteClick = {},
                onSeeAllReviewsClick = {},
                onLogoutClick = {},
            )
        }
    }
}
