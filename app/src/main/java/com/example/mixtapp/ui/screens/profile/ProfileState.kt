package com.example.mixtapp.ui.screens.profile

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.ui.screens.profile.model.PESTANA_PERFIL
import com.example.mixtapp.ui.screens.profile.model.ProfileTabUi
import com.example.mixtapp.ui.screens.profile.model.profileTabs

data class ProfileState(
    // Arranca cargando hasta que llegan mis datos
    val isLoading: Boolean = true,
    // Mi uid de Authentication: con el se abren mis listas de seguidores y seguidos
    val miId: String = "",
    // Mis datos de Firestore; sin mi documento, el usuario sale del correo antes de la arroba
    val usuario: String = "",
    val nombre: String = "",
    val bio: String = "",
    // Mi Instagram sin la @ y su link; vacios si no lo puse
    val instagram: String = "",
    val instagramUrl: String = "",
    val seguidores: Int = 0,
    val seguidos: Int = 0,
    val profileImageUrl: String = "",
    // Aviso para quien todavia no tiene su documento: no es un error de carga
    @StringRes val avisoPerfilRes: Int? = null,
    val subiendoImagen: Boolean = false,
    @StringRes val errorImagenRes: Int? = null,
    // Las pestanas las provee el ViewModel; la elegida se conserva al volver a la pantalla
    val tabs: List<ProfileTabUi> = profileTabs,
    val selectedTabId: String = PESTANA_PERFIL,
    // Cada seccion llega por su propio flujo y trae su propio error: si una falla, las demas se ven
    val favoritos: List<ItemResumenUi> = emptyList(),
    @StringRes val errorFavoritosRes: Int? = null,
    // Solo las mas recientes: todas estan en Mis resenas
    val resenasRecientes: List<ReviewUi> = emptyList(),
    @StringRes val errorResenasRes: Int? = null,
    val guardados: List<ItemResumenUi> = emptyList(),
    @StringRes val errorGuardadosRes: Int? = null,
    // El like o el borrado de una resena que no se pudo hacer
    @StringRes val errorAccionRes: Int? = null,
    // Mientras se borra no se manda otro borrado
    val eliminando: Boolean = false,
    // Error al cargar mis datos: si no hay nada que mostrar, la pantalla deja el motivo y cerrar sesion
    @StringRes val errorMessageRes: Int? = null,
)
