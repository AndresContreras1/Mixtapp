package com.example.mixtapp.ui.screens.profile

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.ProfileUi
import com.example.mixtapp.ui.screens.profile.model.ProfileTabUi

// El perfil es nulable porque puede que todavia no se haya cargado
data class ProfileState(
    val profile: ProfileUi? = null,
    // Las pestanas las provee el ViewModel; el componente solo las pinta
    val tabs: List<ProfileTabUi> = emptyList(),
    val selectedTabId: String = "",
    // Nombre de usuario que sale del correo de Firebase, ya sin la arroba
    val usuario: String = "",
    val profileImageUrl: String = "",
    val subiendoImagen: Boolean = false,
    @StringRes val errorImagenRes: Int? = null,
    @StringRes val errorMessageRes: Int? = null,
)
