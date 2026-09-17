package com.example.mixtapp.data.local

import com.example.mixtapp.data.model.SearchCategoryUi

object LocalSearchCategoriesProvider {
    val categories = listOf(
        SearchCategoryUi(
            id = "release-date",
            title = "Fecha de lanzamiento",
            subtitle = "Novedades y años",
        ),
        SearchCategoryUi(
            id = "most-popular",
            title = "Más populares",
            subtitle = "Los álbumes que todos escuchan",
        ),
        SearchCategoryUi(
            id = "highest-rated",
            title = "Mejor calificados",
            subtitle = "Los mejor calificados por los usuarios de Mixtapp",
        ),
    )
}