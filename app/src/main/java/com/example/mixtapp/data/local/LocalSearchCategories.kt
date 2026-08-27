package com.example.mixtapp.data.local

import com.example.mixtapp.ui.screens.search.model.SearchCategoryUi

object LocalSearchCategories {
    val categories = listOf(
        SearchCategoryUi(
            id = "release-date",
            title = "Release date",
            subtitle = "New releases and years",
        ),
        SearchCategoryUi(
            id = "most-popular",
            title = "Most popular",
            subtitle = "Albums everyone is playing",
        ),
        SearchCategoryUi(
            id = "highest-rated",
            title = "Highest rated",
            subtitle = "Top-rated albums by Mixtapp users",
        ),
    )
}