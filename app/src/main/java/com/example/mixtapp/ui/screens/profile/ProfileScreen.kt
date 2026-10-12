package com.example.mixtapp.ui.screens.profile

import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.local.LocalAlbumProvider
import com.example.mixtapp.data.local.LocalMyReviewsProvider
import com.example.mixtapp.data.model.Album
import com.example.mixtapp.data.model.MyReviewUi
import com.example.mixtapp.ui.screens.myreviews.components.MyReviewCard
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

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    onSettingsClick: () -> Unit,
    onMoreClick: () -> Unit,
    logoutButtonPressed: () -> Unit,
    onReviewClick: (String) -> Unit,
    onSeeAllReviewsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by profileViewModel.uiState.collectAsState()

    // Se pide cada vez que se muestra: al volver de escribir una resena, ya aparece aqui
    LaunchedEffect(Unit) {
        profileViewModel.getProfile()
    }

    // Si falla la carga, el perfil se sigue mostrando con el error arriba,
    // para que la foto y el cierre de sesion sigan disponibles
    if (state.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        ProfileScreenContent(
            misResenas = state.misResenas,
            favoritos = state.favoritos,
            barrasDeCalificacion = state.barrasDeCalificacion,
            usuario = state.usuario,
            nombre = state.nombre,
            bio = state.bio,
            profileImageUrl = state.profileImageUrl,
            subiendoImagen = state.subiendoImagen,
            errorImagenRes = state.errorImagenRes,
            errorMessageRes = state.errorMessageRes,
            onImagePicked = { profileViewModel.subirFotoDePerfil(uri = it) },
            tabs = state.tabs,
            selectedTabId = state.selectedTabId,
            onTabSelected = { profileViewModel.updateSelectedTab(tabId = it) },
            onSettingsClick = onSettingsClick,
            onMoreClick = onMoreClick,
            onLogoutClick = {
                profileViewModel.cerrarSesion()
                logoutButtonPressed()
            },
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
    profileImageUrl: String,
    subiendoImagen: Boolean,
    errorImagenRes: Int?,
    errorMessageRes: Int?,
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
            profileImageUrl = "",
            subiendoImagen = false,
            errorImagenRes = null,
            errorMessageRes = null,
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
