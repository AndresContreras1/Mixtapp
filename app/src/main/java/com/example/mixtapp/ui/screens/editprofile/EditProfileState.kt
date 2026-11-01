package com.example.mixtapp.ui.screens.editprofile

import androidx.annotation.StringRes

data class EditProfileState(
    val nombre: String = "",
    val username: String = "",
    val bio: String = "",
    // Mi usuario de Instagram como lo escribo; al guardar se le quita la @
    val instagram: String = "",
    // Una cuenta creada antes del sprint 10 no tiene documento: al guardar se crea en vez de modificarse
    val sinDocumento: Boolean = false,
    val isLoading: Boolean = false,
    // Error al leer mis datos: no se muestra el formulario, porque guardar reemplazaria el documento
    @StringRes val errorCargaRes: Int? = null,
    val guardando: Boolean = false,
    // La pantalla vuelve al perfil solo cuando Firestore confirma el cambio
    val guardado: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
)
