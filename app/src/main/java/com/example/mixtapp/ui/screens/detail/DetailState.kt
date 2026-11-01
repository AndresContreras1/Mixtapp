package com.example.mixtapp.ui.screens.detail

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.ItemConResenasUi

// El item es nulable porque puede que el id no exista. Sirve igual para un album, una cancion o
// un artista
data class DetailState(
    val itemConResenas: ItemConResenasUi? = null,
    // Mientras se borra mi resena no se manda otro borrado
    val eliminando: Boolean = false,
    // Arranca cargando porque lo primero que hace la pantalla es pedir el item
    val isLoading: Boolean = true,
    // Error de la pantalla: no se pudo cargar el item
    @StringRes val errorMessageRes: Int? = null,
    // Error de un boton (like, guardar, eliminar): la pantalla sigue mostrando el item
    @StringRes val errorAccionRes: Int? = null,
)
