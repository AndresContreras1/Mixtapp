package com.example.mixtapp.ui.screens.mixtapedetail

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.MixtapeUi

// La mixtape es nulable porque puede que el id no exista o que la borren mientras se ve
data class MixtapeDetailState(
    val mixtape: MixtapeUi? = null,
    // Arranca cargando porque lo primero que hace la pantalla es pedir la mixtape
    val isLoading: Boolean = true,
    // Cuando la borro, la pantalla vuelve atras
    val eliminada: Boolean = false,
    // No se pudo cargar: ocupa la pantalla
    @StringRes val errorMessageRes: Int? = null,
    // No se pudo quitar un item o borrarla: la mixtape sigue visible
    @StringRes val errorAccionRes: Int? = null,
)
