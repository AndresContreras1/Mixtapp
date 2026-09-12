package com.example.mixtapp.ui.screens.search

import com.example.mixtapp.ui.screens.search.model.SearchCategoryUi
import com.example.mixtapp.ui.screens.songreview.model.SongReviewUi

data class SearchState(
    val query: String = "",
    val categories: List<SearchCategoryUi> = emptyList(),
    val selectedCategoryId: String? = null,
    val resultados: List<SongReviewUi> = emptyList(),
)
