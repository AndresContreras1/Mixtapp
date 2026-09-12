package com.example.mixtapp.ui.screens.profile

import androidx.annotation.StringRes
import com.example.mixtapp.ui.screens.profile.model.ProfileUi

// El perfil es nulable porque puede que todavia no se haya cargado
data class ProfileState(
    val profile: ProfileUi? = null,
    // Las pestanas las provee el ViewModel; el componente solo las pinta
    val tabs: List<String> = emptyList(),
    val selectedTab: String = "",
    // Nombre de usuario que sale del correo de Firebase, ya sin la arroba
    val usuario: String = "",
    val profileImageUrl: String = "",
    val subiendoImagen: Boolean = false,
    @StringRes val errorImagenRes: Int? = null,
    // Lo pone el ViewModel al cerrar sesion; la navegacion solo lo ejecuta
    val sesionCerrada: Boolean = false,
)
