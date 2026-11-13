package com.example.mixtapp.ui.screens.agregaramixtape

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.MixtapeUi

data class AgregarAMixtapeState(
    // El item que se va a agregar; null mientras llega
    val item: ItemUi? = null,
    val mixtapes: List<MixtapeUi> = emptyList(),
    // Arranca cargando porque lo primero que hace el ViewModel es escuchar mis mixtapes
    val isLoading: Boolean = true,
    // Cuando el item queda agregado, la pantalla vuelve al detalle
    val navigate: Boolean = false,
    // No se pudo cargar: ocupa la pantalla
    @StringRes val errorMessageRes: Int? = null,
    // No se pudo agregar: la lista sigue visible
    @StringRes val errorAccionRes: Int? = null,
)
