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
import com.example.mixtapp.data.model.MixtapeUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.ui.screens.followlist.LISTA_SEGUIDORES
import com.example.mixtapp.ui.screens.followlist.LISTA_SEGUIDOS
import com.example.mixtapp.ui.screens.mixtapes.CreateMixtapeDialog
import com.example.mixtapp.ui.screens.mixtapes.components.MixtapeCard
import com.example.mixtapp.ui.screens.profile.components.GUARDADOS_POR_FILA
import com.example.mixtapp.ui.screens.profile.components.LogoutButton
import com.example.mixtapp.ui.screens.profile.components.ProfileTabs
import com.example.mixtapp.ui.screens.profile.components.SavedItemsRow
import com.example.mixtapp.ui.screens.profile.model.PESTANA_GUARDADOS
import com.example.mixtapp.ui.screens.profile.model.PESTANA_MIXTAPES
import com.example.mixtapp.ui.screens.profile.model.PESTANA_PERFIL
import com.example.mixtapp.ui.screens.profile.model.ProfileTabUi
import com.example.mixtapp.ui.screens.profile.model.profileTabs
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.AppButton
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
    onMixtapeClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by profileViewModel.uiState.collectAsState()

    val onLogoutClick = {
        profileViewModel.cerrarSesion()
        logoutButtonPressed()
    }

    LaunchedEffect(Unit) {
        profileViewModel.escucharMiPerfil()
    }

    val errorMessageRes = state.errorMessageRes

    Box(modifier = modifier.fillMaxSize()) {
        AppBackground()

        when {
            state.isLoading -> {
                LoadingBox()
            }
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
                mixtapes = state.mixtapes,
                errorMixtapesRes = state.errorMixtapesRes,
                onSettingsClick = onSettingsClick,
                onSeguidoresClick = { onFollowListClick(state.miId, LISTA_SEGUIDORES) },
                onSeguidosClick = { onFollowListClick(state.miId, LISTA_SEGUIDOS) },
                onImagePicked = { profileViewModel.subirFotoDePerfil(uri = it) },
                onTabSelected = { profileViewModel.updateSelectedTab(tabId = it) },
                onItemClick = { item -> onItemClick(item.tipo, item.id) },
                onLikeClick = { profileViewModel.sendOrDeleteLike(reviewId = it) },
                onCommentsClick = onCommentsClick,
                onEditClick = { review -> onEditReviewClick(review.item.tipo, review.item.id) },
                onDeleteClick = { profileViewModel.eliminarResena(reviewId = it) },
                onSeeAllReviewsClick = onSeeAllReviewsClick,
                onLogoutClick = onLogoutClick,
                onCreateMixtapeClick = { profileViewModel.setShowCreateMixtapeDialog(true) },
                onMixtapeClick = onMixtapeClick,
            )
        }

        if (state.showCreateMixtapeDialog) {
            CreateMixtapeDialog(
                onDismiss = { profileViewModel.setShowCreateMixtapeDialog(false) },
                onCreate = { nombre, desc -> profileViewModel.crearMixtape(nombre, desc) }
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
    mixtapes: List<MixtapeUi>,
    @StringRes errorMixtapesRes: Int?,
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
    onCreateMixtapeClick: () -> Unit,
    onMixtapeClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
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
                    else -> items(guardados.chunked(GUARDADOS_POR_FILA)) { fila ->
                        SavedItemsRow(items = fila, onItemClick = onItemClick)
                    }
                }
            }
            PESTANA_MIXTAPES -> {
                item {
                    AppButton(
                        texto = stringResource(R.string.crear_mixtape),
                        onClick = onCreateMixtapeClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                when {
                    errorMixtapesRes != null -> item {
                        ErrorMessage(messageRes = errorMixtapesRes)
                    }
                    mixtapes.isEmpty() -> item {
                        EmptyMessage(messageRes = R.string.sin_mixtapes, modifier = Modifier.fillMaxWidth())
                    }
                    else -> items(mixtapes) { mixtape ->
                        MixtapeCard(
                            mixtape = mixtape,
                            onClick = { onMixtapeClick(mixtape.id) },
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
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
