package com.example.mixtapp.ui.screens.profile

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import com.example.mixtapp.data.local.LocalAlbumProvider
import com.example.mixtapp.data.local.LocalMyReviewsProvider
import com.example.mixtapp.data.model.Album
import com.example.mixtapp.data.model.MyReviewUi
import com.example.mixtapp.ui.screens.profile.components.EditReviewCard
import com.example.mixtapp.ui.screens.profile.components.FavoriteSection
import com.example.mixtapp.ui.screens.profile.components.LogoutButton
import com.example.mixtapp.ui.screens.profile.components.MyReviewsTitle
import com.example.mixtapp.ui.screens.profile.components.ProfileAvatarSection
import com.example.mixtapp.ui.screens.profile.components.ProfileHeader
import com.example.mixtapp.ui.screens.profile.components.RatingsSection
import com.example.mixtapp.ui.screens.profile.model.ProfileTabUi
import com.example.mixtapp.ui.screens.profile.model.profileTabs
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.MyReviewCard

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    onSettingsClick: () -> Unit,
    onMoreClick: () -> Unit,
    logoutButtonPressed: () -> Unit,
    onReviewClick: (String) -> Unit,
    onSeguidoresClick: (String) -> Unit,
    onSeguidosClick: (String) -> Unit,
    onSeeAllReviewsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by profileViewModel.uiState.collectAsState()

    // Cerrar sesion esta en el perfil y tambien en el error, para que nadie quede atrapado
    val onLogoutClick = {
        profileViewModel.cerrarSesion()
        logoutButtonPressed()
    }

    // Se pide cada vez que se muestra: al volver de escribir una resena, ya aparece aqui
    LaunchedEffect(Unit) {
        profileViewModel.getProfile()
    }

    when {
        state.isLoading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        // Sin resenas cargadas solo se muestra el motivo del error y la salida para cerrar sesion
        state.errorMessageRes != null && state.misResenas.isEmpty() -> {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(state.errorMessageRes ?: R.string.error_cargar_contenido))

                LogoutButton(onLogoutClick = onLogoutClick)
            }
        }
        else -> ProfileScreenContent(
            misResenas = state.misResenas,
            favoritos = state.favoritos,
            barrasDeCalificacion = state.barrasDeCalificacion,
            usuario = state.usuario,
            nombre = state.nombre,
            bio = state.bio,
            seguidores = state.seguidores,
            seguidos = state.seguidos,
            onSeguidoresClick = { onSeguidoresClick(state.usuarioId) },
            onSeguidosClick = { onSeguidosClick(state.usuarioId) },
            profileImageUrl = state.profileImageUrl,
            subiendoImagen = state.subiendoImagen,
            errorImagenRes = state.errorImagenRes,
            errorMessageRes = state.errorMessageRes,
            avisoPerfilRes = state.avisoPerfilRes,
            onImagePicked = { profileViewModel.subirFotoDePerfil(uri = it) },
            tabs = state.tabs,
            selectedTabId = state.selectedTabId,
            onTabSelected = { profileViewModel.updateSelectedTab(tabId = it) },
            onSettingsClick = onSettingsClick,
            onMoreClick = onMoreClick,
            onLogoutClick = onLogoutClick,
            onReviewClick = onReviewClick,
            onSeeAllReviewsClick = onSeeAllReviewsClick,
            resenaEnEdicionId = state.resenaEnEdicionId,
            calificacionEditada = state.calificacionEditada,
            textoEditado = state.textoEditado,
            guardandoEdicion = state.guardandoEdicion,
            errorEdicionRes = state.errorEdicionRes,
            onEditClick = { profileViewModel.empezarEdicion(resena = it) },
            onDeleteClick = { profileViewModel.eliminarResena(reviewId = it) },
            onCalificacionEditadaChange = { profileViewModel.updateCalificacionEditada(rating = it) },
            onTextoEditadoChange = { profileViewModel.updateTextoEditado(texto = it) },
            onGuardarEdicionClick = { profileViewModel.guardarEdicion() },
            onCancelarEdicionClick = { profileViewModel.cancelarEdicion() },
            modifier = modifier
        )
    }
}

@Composable
fun ProfileScreenContent(
    misResenas: List<MyReviewUi>,
    favoritos: List<Album>,
    barrasDeCalificacion: List<Float>,
    usuario: String,
    nombre: String,
    bio: String,
    seguidores: Int,
    seguidos: Int,
    onSeguidoresClick: () -> Unit,
    onSeguidosClick: () -> Unit,
    profileImageUrl: String,
    subiendoImagen: Boolean,
    errorImagenRes: Int?,
    errorMessageRes: Int?,
    avisoPerfilRes: Int?,
    onImagePicked: (Uri) -> Unit,
    tabs: List<ProfileTabUi>,
    selectedTabId: String,
    onTabSelected: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onMoreClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onReviewClick: (String) -> Unit,
    onSeeAllReviewsClick: () -> Unit,
    // Si coincide con una resena, esa tarjeta se muestra como formulario de edicion
    resenaEnEdicionId: String?,
    calificacionEditada: Int,
    textoEditado: String,
    guardandoEdicion: Boolean,
    errorEdicionRes: Int?,
    onEditClick: (MyReviewUi) -> Unit,
    onDeleteClick: (String) -> Unit,
    onCalificacionEditadaChange: (Int) -> Unit,
    onTextoEditadoChange: (String) -> Unit,
    onGuardarEdicionClick: () -> Unit,
    onCancelarEdicionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            ProfileHeader(
                usuario = usuario,
                tabs = tabs,
                selectedTabId = selectedTabId,
                onTabSelected = onTabSelected,
                onSettingsClick = onSettingsClick,
                onMoreClick = onMoreClick
            )

            // Mis resenas son una lista dinamica: cada una es un item de la LazyColumn
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    ProfileAvatarSection(
                        nombre = nombre,
                        bio = bio,
                        seguidores = seguidores,
                        seguidos = seguidos,
                        onSeguidoresClick = onSeguidoresClick,
                        onSeguidosClick = onSeguidosClick,
                        profileImageUrl = profileImageUrl,
                        subiendoImagen = subiendoImagen,
                        errorImagenRes = errorImagenRes,
                        onImagePicked = onImagePicked,
                        reviewsCount = misResenas.size,
                        favoritosCount = favoritos.size,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    ErrorMessage(
                        messageRes = errorMessageRes,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    ErrorMessage(
                        messageRes = avisoPerfilRes,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    FavoriteSection(favoriteAlbums = favoritos)

                    MyReviewsTitle(
                        sinResenas = misResenas.isEmpty(),
                        onSeeAllClick = onSeeAllReviewsClick
                    )
                }

                items(misResenas.size) { index ->
                    val review = misResenas[index]

                    if (review.id == resenaEnEdicionId) {
                        EditReviewCard(
                            review = review,
                            rating = calificacionEditada,
                            texto = textoEditado,
                            guardando = guardandoEdicion,
                            errorMessageRes = errorEdicionRes,
                            onRatingChange = onCalificacionEditadaChange,
                            onTextoChange = onTextoEditadoChange,
                            onGuardarClick = onGuardarEdicionClick,
                            onCancelarClick = onCancelarEdicionClick,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    } else {
                        MyReviewCard(
                            review = review,
                            onReviewClick = onReviewClick,
                            showEditButton = true,
                            onEditClick = onEditClick,
                            onDeleteClick = onDeleteClick,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))

                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))

                    RatingsSection(
                        ratingBars = barrasDeCalificacion,
                        onMasActividadClick = onSeeAllReviewsClick,
                        modifier = Modifier.padding(top = 24.dp, bottom = 80.dp)
                    )

                    LogoutButton(onLogoutClick = onLogoutClick)
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
fun ProfileScreenPreview() {
    MixtappTheme(darkTheme = true) {
        ProfileScreenContent(
            misResenas = LocalMyReviewsProvider.reviews,
            favoritos = LocalAlbumProvider.albums.take(2),
            barrasDeCalificacion = listOf(0f, 0f, 0f, 0f, 1f, 0.5f),
            usuario = "usuario",
            nombre = "Usuario de Mixtapp",
            bio = "Escucho rock y metal todo el dia",
            seguidores = 12,
            seguidos = 34,
            onSeguidoresClick = {},
            onSeguidosClick = {},
            profileImageUrl = "",
            subiendoImagen = false,
            errorImagenRes = null,
            errorMessageRes = null,
            avisoPerfilRes = null,
            onImagePicked = {},
            tabs = profileTabs,
            selectedTabId = profileTabs.first().id,
            onTabSelected = {},
            onSettingsClick = {},
            onMoreClick = {},
            onLogoutClick = {},
            onReviewClick = {},
            onSeeAllReviewsClick = {},
            resenaEnEdicionId = null,
            calificacionEditada = 0,
            textoEditado = "",
            guardandoEdicion = false,
            errorEdicionRes = null,
            onEditClick = {},
            onDeleteClick = {},
            onCalificacionEditadaChange = {},
            onTextoEditadoChange = {},
            onGuardarEdicionClick = {},
            onCancelarEdicionClick = {}
        )
    }
}
