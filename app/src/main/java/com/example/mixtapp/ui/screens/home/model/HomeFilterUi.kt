package com.example.mixtapp.ui.screens.home.model

import androidx.annotation.StringRes
import com.example.mixtapp.R

// Un filtro del inicio: cambia lo que muestran las filas de canciones, albumes y artistas
data class HomeFilterUi(
    val id: String,
    @StringRes val label: Int
)

// Mezcla los items de los generos que me gustan (mis likes)
const val FILTRO_PARA_TI = "paraTi"

// Solo lo que esta en el Top Colombia, por su puesto
const val FILTRO_TENDENCIAS = "tendencias"

// Lo que resenaron los usuarios que sigo
const val FILTRO_SEGUIDOS = "seguidos"

val homeFilters = listOf(
    HomeFilterUi(id = FILTRO_PARA_TI, label = R.string.for_you),
    HomeFilterUi(id = FILTRO_TENDENCIAS, label = R.string.trending),
    HomeFilterUi(id = FILTRO_SEGUIDOS, label = R.string.friends)
)
