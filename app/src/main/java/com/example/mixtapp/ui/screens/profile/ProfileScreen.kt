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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
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
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.ui.screens.followlist.LISTA_SEGUIDORES
import com.example.mixtapp.ui.screens.followlist.LISTA_SEGUIDOS
import com.example.mixtapp.ui.screens.profile.components.GUARDADOS_POR_FILA
import com.example.mixtapp.ui.screens.profile.components.LogoutButton
import com.example.mixtapp.ui.screens.profile.components.PickImageButton
import com.example.mixtapp.ui.screens.profile.components.ProfileTabs
import com.example.mixtapp.ui.screens.profile.components.SavedItemsRow
import com.example.mixtapp.ui.screens.profile.components.TarjetaDeTuAnio
import com.example.mixtapp.ui.screens.profile.model.PESTANA_GUARDADOS
import com.example.mixtapp.ui.screens.profile.model.PESTANA_MIXTAPES
import com.example.mixtapp.ui.screens.profile.model.PESTANA_PERFIL
import com.example.mixtapp.ui.screens.profile.model.profileTabs
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.AppButton
import com.example.mixtapp.ui.utils.BarraSuperior
import com.example.mixtapp.ui.utils.EmptyMessage
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.EspacioDelFab
import com.example.mixtapp.ui.utils.FavoriteSection
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.MixtapeCard
import com.example.mixtapp.ui.utils.OpcionUi
import com.example.mixtapp.ui.utils.ProfileHeader
import com.example.mixtapp.ui.utils.ReviewCard
import com.example.mixtapp.ui.utils.ScreenTitle
import com.example.mixtapp.ui.utils.SectionTitle
import com.example.mixtapp.ui.utils.TonoDeFondo

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    onEditProfileClick: () -> Unit,
    logoutButtonPressed: () -> Unit,
    onItemClick: (String, String) -> Unit,
    onCommentsClick: (String) -> Unit,
    onEditReviewClick: (String, String) -> Unit,
    onFollowListClick: (userId: String, tipo: String) -> Unit,
    onSeeAllReviewsClick: () -> Unit,
    onResumenClick: () -> Unit,
    onCrearMixtapeClick: () -> Unit,
    onMixtapeClick: (String) -> Unit,
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
        AppBackground(tono = TonoDeFondo.Perfil)

        Column(modifier = Modifier.fillMaxSize()) {
            // El titulo y Ajustes quedan fijos arriba, tambien mientras carga o si hay un error
            BarraSuperior {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ScreenTitle(
                        title = stringResource(R.string.nav_profile),
                        modifier = Modifier.weight(1f),
                        onBackClick = null,
                    )

                    IconButton(onClick = onEditProfileClick) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.ajustes),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
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
                        cargandoFavoritos = state.cargandoFavoritos,
                        errorFavoritosRes = state.errorFavoritosRes,
                        resenasRecientes = state.resenasRecientes,
                        cargandoResenas = state.cargandoResenas,
                        errorResenasRes = state.errorResenasRes,
                        errorAccionRes = state.errorAccionRes,
                        guardados = state.guardados,
                        cargandoGuardados = state.cargandoGuardados,
                        errorGuardadosRes = state.errorGuardadosRes,
                        mixtapes = state.mixtapes,
                        cargandoMixtapes = state.cargandoMixtapes,
                        errorMixtapesRes = state.errorMixtapesRes,
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
                        onResumenClick = onResumenClick,
                        onCrearMixtapeClick = onCrearMixtapeClick,
                        onMixtapeClick = onMixtapeClick,
                        onLogoutClick = onLogoutClick,
                    )
                }
            }
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
    tabs: List<OpcionUi>,
    selectedTabId: String,
    favoritos: List<ItemResumenUi>,
    cargandoFavoritos: Boolean,
    @StringRes errorFavoritosRes: Int?,
    resenasRecientes: List<ReviewUi>,
    cargandoResenas: Boolean,
    @StringRes errorResenasRes: Int?,
    @StringRes errorAccionRes: Int?,
    guardados: List<ItemResumenUi>,
    cargandoGuardados: Boolean,
    @StringRes errorGuardadosRes: Int?,
    mixtapes: List<MixtapeUi>,
    cargandoMixtapes: Boolean,
    @StringRes errorMixtapesRes: Int?,
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
    onResumenClick: () -> Unit,
    onCrearMixtapeClick: () -> Unit,
    onMixtapeClick: (String) -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Toda la pantalla es una sola lista: el encabezado y las pestanas suben con el contenido
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        // Abajo queda espacio para que el boton + no tape el ultimo elemento
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = EspacioDelFab),
        // Los bloques (identidad, pestanas, contenido) van con aire entre ellos
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
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
                // Mi foto se cambia al tocarla
                fotoEditable = {
                    PickImageButton(
                        fotoUrl = profileImageUrl,
                        action = onImagePicked,
                        cargando = subiendoImagen,
                    )
                },
            ) {
                // La foto que no se pudo subir y los avisos de mis datos
                ErrorMessage(
                    messageRes = errorImagenRes,
                    modifier = Modifier.padding(top = 8.dp)
                )
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
                )
            }
        }

        when (selectedTabId) {
            PESTANA_GUARDADOS -> {
                // Que es Guardados, para no confundirlo con Favoritos
                item {
                    Text(
                        text = stringResource(R.string.guardados_descripcion),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                listaDelPerfil(
                    cargando = cargandoGuardados,
                    vacia = guardados.isEmpty(),
                    errorMessageRes = errorGuardadosRes,
                    vaciaRes = R.string.sin_guardados,
                ) {
                    // La cuadricula se arma con filas de tres: cada fila es un item de la lista
                    items(guardados.chunked(GUARDADOS_POR_FILA)) { fila ->
                        SavedItemsRow(items = fila, onItemClick = onItemClick)
                    }
                }
            }
            PESTANA_MIXTAPES -> {
                // Crear va arriba: se ve aunque todavia no tenga mixtapes
                item {
                    AppButton(
                        texto = stringResource(R.string.crear_mixtape),
                        onClick = onCrearMixtapeClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btnIrACrearMixtape"),
                        cargando = false,
                    )
                }

                listaDelPerfil(
                    cargando = cargandoMixtapes,
                    vacia = mixtapes.isEmpty(),
                    errorMessageRes = errorMixtapesRes,
                    vaciaRes = R.string.sin_mixtapes,
                ) {
                    items(mixtapes, key = { it.id }) { mixtape ->
                        MixtapeCard(
                            mixtape = mixtape,
                            onClick = { onMixtapeClick(mixtape.id) },
                        )
                    }
                }
            }
            else -> {
                // Mis numeros de la app en tarjetas que se van descubriendo
                item {
                    TarjetaDeTuAnio(onClick = onResumenClick)
                }

                item {
                    FavoriteSection(
                        favoritos = favoritos,
                        cargando = cargandoFavoritos,
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

                listaDelPerfil(
                    cargando = cargandoResenas,
                    vacia = resenasRecientes.isEmpty(),
                    errorMessageRes = errorResenasRes,
                    vaciaRes = R.string.sin_mis_resenas,
                ) {
                    items(resenasRecientes) { review ->
                        ReviewCard(
                            review = review,
                            // Soy yo: tocar mi nombre no abre nada
                            onAuthorClick = {},
                            onLikeClick = onLikeClick,
                            onCommentsClick = onCommentsClick,
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

// Una lista de mi perfil (mis guardados, mis mixtapes, mi actividad): la carga mientras llega, el
// aviso del error arriba (si el listener falla despues de cargar, queda lo de antes), el mensaje si
// esta vacia y sus elementos
private fun LazyListScope.listaDelPerfil(
    cargando: Boolean,
    vacia: Boolean,
    @StringRes errorMessageRes: Int?,
    @StringRes vaciaRes: Int,
    elementos: LazyListScope.() -> Unit,
) {
    if (cargando) {
        item { LoadingBox(modifier = Modifier.height(120.dp)) }
        return
    }

    if (errorMessageRes != null) {
        item { ErrorMessage(messageRes = errorMessageRes) }
    } else if (vacia) {
        item { EmptyMessage(messageRes = vaciaRes, modifier = Modifier.fillMaxWidth()) }
    }

    elementos()
}

@Composable
@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
fun ProfileScreenPreview() {
    MixtappTheme(darkTheme = true) {
        val usuario = LocalPreviewProvider.usuario

        Box {
            AppBackground(tono = TonoDeFondo.Perfil)

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
                cargandoFavoritos = false,
                errorFavoritosRes = null,
                resenasRecientes = listOf(LocalPreviewProvider.miReview),
                cargandoResenas = false,
                errorResenasRes = null,
                errorAccionRes = null,
                guardados = LocalPreviewProvider.favoritos,
                cargandoGuardados = false,
                errorGuardadosRes = null,
                mixtapes = emptyList(),
                cargandoMixtapes = false,
                errorMixtapesRes = null,
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
                onResumenClick = {},
                onCrearMixtapeClick = {},
                onMixtapeClick = {},
                onLogoutClick = {},
            )
        }
    }
}
