package com.example.mixtapp.ui.screens.home.model

import androidx.annotation.StringRes
import com.example.mixtapp.R

data class HomeFilterUi(
    val id: String,
    @StringRes val label: Int
)

const val FILTRO_PARA_TI = "paraTi"
const val FILTRO_TENDENCIAS = "tendencias"
const val FILTRO_SEGUIDOS = "seguidos"

// Funcionalidad adicional: ordenar el catalogo de albumes
const val FILTRO_TOP_RATING = "topRating"
const val FILTRO_ANIO = "anio"
const val FILTRO_ALFABETICO = "alfabetico"

val homeFilters = listOf(
    HomeFilterUi(id = FILTRO_PARA_TI, label = R.string.for_you),
    HomeFilterUi(id = FILTRO_TENDENCIAS, label = R.string.trending),
    HomeFilterUi(id = FILTRO_SEGUIDOS, label = R.string.friends),
    HomeFilterUi(id = FILTRO_TOP_RATING, label = R.string.home_filtro_mejor_calificados),
    HomeFilterUi(id = FILTRO_ANIO, label = R.string.home_filtro_por_anio),
    HomeFilterUi(id = FILTRO_ALFABETICO, label = R.string.home_filtro_alfabetico)
)
