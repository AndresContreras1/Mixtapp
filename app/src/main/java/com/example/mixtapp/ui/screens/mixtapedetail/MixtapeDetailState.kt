package com.example.mixtapp.ui.screens.mixtapedetail

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.MixtapeConItemsUi

// La mixtape es nulable porque puede que el id no exista o que la borren mientras se ve
data class MixtapeDetailState(
    val mixtapeConItems: MixtapeConItemsUi? = null,
    // Se pone en true al empezar a escuchar la mixtape y en false cuando llega o falla
    val isLoading: Boolean = false,
    // Cuando la borro, la pantalla vuelve atras
    val eliminada: Boolean = false,
    // No se pudo cargar: ocupa la pantalla, o sale encima de la lista si ya habia cargado
    @StringRes val errorMessageRes: Int? = null,
    // No se pudo quitar un item o borrarla: la mixtape sigue visible
    @StringRes val errorAccionRes: Int? = null,
)
