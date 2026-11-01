package com.example.mixtapp.data.model

import androidx.annotation.StringRes

// Una tarjeta de Buscar: una forma de ordenar el catalogo o un genero
data class SearchCategoryUi(
    // En los generos es el mismo texto que guarda la base en el campo genero
    val id: String,
    @StringRes val titleRes: Int,
    val imagenUrl: String,
    val esGenero: Boolean,
)

const val CATEGORIA_FECHA_LANZAMIENTO = "release-date"
const val CATEGORIA_MAS_POPULARES = "most-popular"
const val CATEGORIA_MEJOR_CALIFICADOS = "highest-rated"
