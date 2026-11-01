package com.example.mixtapp.ui.screens.datoscuriosos

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.ItemUi

// El item es nulable porque puede que el id no exista. Los datos curiosos vienen dentro del item
data class DatosCuriososState(
    val item: ItemUi? = null,
    // Arranca cargando porque lo primero que hace la pantalla es pedir el item
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null,
)
