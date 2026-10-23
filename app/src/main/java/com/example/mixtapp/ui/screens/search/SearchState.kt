package com.example.mixtapp.ui.screens.search

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.SearchCategoryUi
import com.example.mixtapp.data.model.AlbumReviewsUi

data class SearchState(
    val query: String = "",
    val categories: List<SearchCategoryUi> = emptyList(),
    val selectedCategoryId: String? = null,
    // Todos los albumes guardados: buscar y ordenar no los vuelve a pedir
    val todos: List<AlbumReviewsUi> = emptyList(),
    // Ya vienen filtrados u ordenados por el ViewModel
    val resultados: List<AlbumReviewsUi> = emptyList(),
    // Arranca cargando porque lo primero que hace la pantalla es pedir los albumes
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null,
)
