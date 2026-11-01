package com.example.mixtapp.ui.screens.home.model

import androidx.annotation.StringRes
import com.example.mixtapp.R

// Un filtro del inicio: cambia lo que muestran las filas de canciones, albumes y artistas
data class HomeFilterUi(
    val id: String,
    @StringRes val label: Int
)

// Lo mas popular de los generos que me gustan (mis likes y mis resenas)
const val FILTRO_PARA_TI = "paraTi"

// Musica nueva para mi: de mis generos, pero sin mi like ni mi resena todavia
const val FILTRO_DESCUBRE = "descubre"

// Lo que esta en la lista de Deezer de mis generos, por su puesto
const val FILTRO_TENDENCIAS = "tendencias"

// Lo que resenaron los usuarios que sigo
const val FILTRO_SEGUIDOS = "seguidos"

val homeFilters = listOf(
    HomeFilterUi(id = FILTRO_PARA_TI, label = R.string.for_you),
    HomeFilterUi(id = FILTRO_DESCUBRE, label = R.string.descubre),
    HomeFilterUi(id = FILTRO_TENDENCIAS, label = R.string.trending),
    HomeFilterUi(id = FILTRO_SEGUIDOS, label = R.string.friends)
)
