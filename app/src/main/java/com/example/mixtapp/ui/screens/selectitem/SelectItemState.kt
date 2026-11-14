package com.example.mixtapp.ui.screens.selectitem

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.ItemConResenasUi
import com.example.mixtapp.ui.screens.selectitem.model.FILTRO_TODOS
import com.example.mixtapp.ui.screens.selectitem.model.SelectItemFilterUi
import com.example.mixtapp.ui.screens.selectitem.model.selectItemFilters

data class SelectItemState(
    val query: String = "",
    val filters: List<SelectItemFilterUi> = selectItemFilters,
    val selectedFilterId: String = FILTRO_TODOS,
    // Todo el catalogo con sus estrellas: el texto y el filtro trabajan sobre esta lista
    val catalogo: List<ItemConResenasUi> = emptyList(),
    // Ya vienen filtrados y ordenados por el ViewModel
    val resultados: List<ItemConResenasUi> = emptyList(),
    // Se pone en true al pedir el catalogo y en false cuando llega o falla
    val isLoading: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
)
