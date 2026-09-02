package com.example.mixtapp.ui.screens.search

import com.example.mixtapp.ui.screens.search.model.SearchCategoryUi

data class SearchState(
    val query: String = "",
    val categories: List<SearchCategoryUi> = emptyList(),
    val selectedCategoryId: String? = null,
)
