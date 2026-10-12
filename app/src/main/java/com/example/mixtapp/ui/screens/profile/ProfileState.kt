package com.example.mixtapp.ui.screens.profile

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.Album
import com.example.mixtapp.data.model.MyReviewUi
import com.example.mixtapp.ui.screens.profile.model.ProfileTabUi

data class ProfileState(
    val misResenas: List<MyReviewUi> = emptyList(),
    // Los favoritos y las barras de calificaciones se calculan con mis resenas
    val favoritos: List<Album> = emptyList(),
    val barrasDeCalificacion: List<Float> = emptyList(),
    // Arranca cargando porque lo primero que hace la pantalla es pedir mis resenas
    val isLoading: Boolean = true,
    // Las pestanas las provee el ViewModel; el componente solo las pinta
    val tabs: List<ProfileTabUi> = emptyList(),
    val selectedTabId: String = "",
    // Nombre de usuario que sale del correo de Firebase, ya sin la arroba
    val usuario: String = "",
    val profileImageUrl: String = "",
    val subiendoImagen: Boolean = false,
    // La resena que se esta editando en esta misma pantalla y lo que lleva escrito
    val resenaEnEdicionId: String? = null,
    val calificacionEditada: Int = 0,
    val textoEditado: String = "",
    val guardandoEdicion: Boolean = false,
    @StringRes val errorEdicionRes: Int? = null,
    @StringRes val errorImagenRes: Int? = null,
    // Mientras se borra no se manda otro DELETE
    val eliminando: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
)
