package com.example.mixtapp.ui.screens.myreviews

import com.example.mixtapp.ui.screens.myreviews.model.MyReviewFilterUi
import com.example.mixtapp.ui.screens.myreviews.model.MyReviewUi

data class MyReviewsState(
    val username: String = "",
    val joinDate: String = "",
    // Ya vienen ordenadas o filtradas por el ViewModel
    val reviews: List<MyReviewUi> = emptyList(),
    // Los filtros los provee el ViewModel; el componente solo los pinta
    val filters: List<MyReviewFilterUi> = emptyList(),
    val selectedFilterId: String = "",
)
