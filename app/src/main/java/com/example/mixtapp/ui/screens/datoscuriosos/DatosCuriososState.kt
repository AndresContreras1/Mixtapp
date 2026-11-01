package com.example.mixtapp.ui.screens.datoscuriosos

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.HistoriaUi
import com.example.mixtapp.data.model.ItemUi

// El item es nulable porque puede que el id no exista. Los datos curiosos llegan aparte, en la
// historia del item, por secciones
data class DatosCuriososState(
    val item: ItemUi? = null,
    // null si el item no tiene datos curiosos o no se pudieron cargar
    val historia: HistoriaUi? = null,
    // Arranca cargando porque lo primero que hace la pantalla es pedir el item
    val isLoading: Boolean = true,
    // El error del item tapa la pantalla; el de la historia va debajo del encabezado
    @StringRes val errorMessageRes: Int? = null,
    @StringRes val errorHistoriaRes: Int? = null,
)
