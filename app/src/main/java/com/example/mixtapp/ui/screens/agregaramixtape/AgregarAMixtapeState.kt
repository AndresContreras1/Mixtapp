package com.example.mixtapp.ui.screens.agregaramixtape

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.MixtapeUi

data class AgregarAMixtapeState(
    // El item que se va a agregar; null mientras llega
    val item: ItemUi? = null,
    // Mis mixtapes; cada una dice si ya tiene el item
    val mixtapes: List<MixtapeUi> = emptyList(),
    // Se pone en true al empezar a escuchar mis mixtapes y en false cuando llegan o fallan
    val isLoading: Boolean = false,
    // Cuando el item queda agregado, la pantalla vuelve al detalle
    val navigate: Boolean = false,
    // No se pudieron cargar mis mixtapes: ocupa la pantalla
    @StringRes val errorMessageRes: Int? = null,
    // No se pudo leer el item: sale arriba y no se puede agregar
    @StringRes val errorItemRes: Int? = null,
    // No se pudo agregar: la lista sigue visible
    @StringRes val errorAccionRes: Int? = null,
)
