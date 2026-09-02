package com.example.mixtapp.ui.screens.myreviews

import com.example.mixtapp.ui.screens.myreviews.model.MyReviewUi

data class MyReviewsState(
    val username: String = "",
    val joinDate: String = "",
    // Ya vienen ordenadas o filtradas por el ViewModel
    val reviews: List<MyReviewUi> = emptyList(),
    val selectedFilter: String = "Recent",
)
