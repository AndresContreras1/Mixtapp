package com.example.mixtapp.ui.screens.search

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.SearchCategoryUi
import com.example.mixtapp.data.model.SongReviewUi

data class SearchState(
    val query: String = "",
    val categories: List<SearchCategoryUi> = emptyList(),
    val selectedCategoryId: String? = null,
    // Todos los albumes del backend: buscar y ordenar no los vuelve a pedir
    val todos: List<SongReviewUi> = emptyList(),
    // Ya vienen filtrados u ordenados por el ViewModel
    val resultados: List<SongReviewUi> = emptyList(),
    val isLoading: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
)
