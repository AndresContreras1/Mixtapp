package com.example.mixtapp.ui.screens.search

import androidx.lifecycle.ViewModel
import com.example.mixtapp.data.local.LocalSearchCategories
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SearchViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SearchState())
    val uiState: StateFlow<SearchState> = _uiState.asStateFlow()

    // No recibe parametros, asi que los datos se cargan al crear el ViewModel
    init {
        getCategories()
    }

    private fun getCategories() {
        _uiState.update { it.copy(categories = LocalSearchCategories.categories) }
    }

    fun updateQuery(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun updateSelectedCategory(categoryId: String) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }
}
