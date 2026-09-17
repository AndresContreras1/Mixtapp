package com.example.mixtapp.data.model

data class SearchCategoryUi(
    val id: String,
    val title: String,
    val subtitle: String,
)

const val CATEGORIA_FECHA_LANZAMIENTO = "release-date"
const val CATEGORIA_MAS_POPULARES = "most-popular"
const val CATEGORIA_MEJOR_CALIFICADOS = "highest-rated"
