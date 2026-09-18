package com.example.mixtapp.ui.screens.search

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.SearchCategoryUi
import com.example.mixtapp.data.model.SongReviewUi

data class SearchState(
    val query: String = "",
    val categories: List<SearchCategoryUi> = emptyList(),
    val selectedCategoryId: String? = null,
    val resultados: List<SongReviewUi> = emptyList(),
    @StringRes val errorMessageRes: Int? = null,
)
