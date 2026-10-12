package com.example.mixtapp.ui.screens.myreviews

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.MyReviewUi
import com.example.mixtapp.ui.screens.myreviews.model.MyReviewFilterUi

data class MyReviewsState(
    val username: String = "",
    val iniciales: String = "",
    // Todas mis resenas, para cambiar de filtro sin volver a pedirlas
    val todas: List<MyReviewUi> = emptyList(),
    // Ya vienen ordenadas o filtradas por el ViewModel
    val reviews: List<MyReviewUi> = emptyList(),
    // Los filtros los provee el ViewModel; el componente solo los pinta
    val filters: List<MyReviewFilterUi> = emptyList(),
    val selectedFilterId: String = "",
    val isLoading: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
)
