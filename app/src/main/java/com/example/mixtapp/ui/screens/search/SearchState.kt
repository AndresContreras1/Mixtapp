package com.example.mixtapp.ui.screens.search

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.SearchCategoryUi
import com.example.mixtapp.data.model.SongReviewUi
import com.example.mixtapp.ui.screens.search.model.SearchResultUi

data class SearchState(
    val query: String = "",
    val categories: List<SearchCategoryUi> = emptyList(),
    val selectedCategoryId: String? = null,
    // Todos los albumes guardados: buscar y ordenar no los vuelve a pedir
    val todos: List<SongReviewUi> = emptyList(),
    val resultados: List<SearchResultUi> = emptyList(),
    val isLoading: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
)
